/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.e2e;

import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.support.PostgreSqlDataSourceHelper;
import org.apache.commons.dbcp2.BasicDataSource;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * This wrapper provides postgres containers as part of HA cluster with repmgr configured.
 * <p>
 * If master goes down, repmgr will ensure any of the standby nodes takes the primary role.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
final class PostgreSqlClusterWrapper implements AutoCloseable {

    static final Duration WAIT_INTERVAL_SECONDS = Duration.ofSeconds(100L);
    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final String IMAGE_TAG = preparePostgresBitnamiVersion();
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlClusterWrapper.class);
    private static final Duration STARTUP_TIMEOUT = Duration.ofSeconds(40L);

    private final String primaryAlias;
    private final String standbyAlias;
    private final Network network;
    private final JdbcDatabaseContainer<?> containerForPrimary;
    private final JdbcDatabaseContainer<?> containerForStandBy;
    private final BasicDataSource dataSourceForPrimary;
    private final BasicDataSource dataSourceForStandBy;

    PostgreSqlClusterWrapper() {
        // REPMGR_NODE_NAME must end with a number, so aliases must also
        // To avoid a ConflictException when starting the container, aliases must be unique if there is more than one instance of PostgresSqlClusterWrapper
        final UUID uuid = UUID.randomUUID();
        this.primaryAlias = String.format("pg-%s-0", uuid);
        this.standbyAlias = String.format("pg-%s-1", uuid);
        this.network = Network.newNetwork();
        // Primary node
        final WaitStrategy waitStrategyForPrimary = new LogMessageWaitStrategy()
                .withRegEx(".*Starting repmgrd.*\\s")
                .withStartupTimeout(STARTUP_TIMEOUT);
        this.containerForPrimary = createContainerAndInitWith(this::primaryEnvVarsMap, primaryAlias, waitStrategyForPrimary);
        // Standby node
        final WaitStrategy waitStrategyForStandBy = new LogMessageWaitStrategy()
                .withRegEx(".*starting monitoring of node.*\\s")
                .withStartupTimeout(STARTUP_TIMEOUT);
        this.containerForStandBy = createContainerAndInitWith(this::standbyEnvVarsMap, standbyAlias, waitStrategyForStandBy);

        this.containerForPrimary.start();
        Awaitility.await("Ensure primary is ready")
                .atMost(STARTUP_TIMEOUT)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForPrimary.getLogs().contains("database system is ready to accept connections"));
        this.containerForStandBy.start();
        Awaitility.await("Ensure cluster is ready")
                .atMost(STARTUP_TIMEOUT)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("started streaming WAL from primary"));

        this.dataSourceForPrimary = PostgreSqlDataSourceHelper.buildDataSource(containerForPrimary);
        this.dataSourceForStandBy = PostgreSqlDataSourceHelper.buildDataSource(containerForStandBy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            dataSourceForStandBy.close();
        } catch (SQLException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        try {
            dataSourceForPrimary.close();
        } catch (SQLException ex) {
            LOGGER.warn(ex.getMessage(), ex);
        }
        containerForStandBy.close();
        containerForPrimary.close();
    }

    @Nonnull
    public DataSource getDataSourceForPrimary() {
        throwErrorIfNotInitialized();
        return dataSourceForPrimary;
    }

    @Nonnull
    public DataSource getDataSourceForStandBy() {
        throwErrorIfNotInitialized();
        return dataSourceForStandBy;
    }

    @Nonnull
    public String getFirstContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", primaryAlias, containerForPrimary.getFirstMappedPort(), containerForPrimary.getDatabaseName());
    }

    @Nonnull
    public String getSecondContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", standbyAlias, containerForStandBy.getFirstMappedPort(), containerForStandBy.getDatabaseName());
    }

    public void stopFirstContainer() {
        containerForPrimary.stop();
        LOGGER.info("Waiting for standby will be promoted to primary");
        Awaitility.await("Promoting standby to primary")
                .atMost(WAIT_INTERVAL_SECONDS)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("promoting standby to primary"));
        Awaitility.await("Standby promoted to primary")
                .atMost(WAIT_INTERVAL_SECONDS)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("standby promoted to primary after"));
    }

    @Nonnull
    private Map<String, String> primaryEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", primaryAlias);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", primaryAlias, standbyAlias));
        envVarsMap.put("REPMGR_NODE_NAME", primaryAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", primaryAlias);
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        envVarsMap.put("REPMGR_CONNECT_TIMEOUT", "1");
        envVarsMap.put("REPMGR_RECONNECT_ATTEMPTS", "1");
        envVarsMap.put("REPMGR_RECONNECT_INTERVAL", "1");
        return envVarsMap;
    }

    @Nonnull
    private Map<String, String> standbyEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", primaryAlias);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", primaryAlias, standbyAlias));
        envVarsMap.put("REPMGR_NODE_NAME", standbyAlias);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", standbyAlias);
        envVarsMap.put("REPMGR_PORT_NUMBER", "5432");
        envVarsMap.put("REPMGR_CONNECT_TIMEOUT", "1");
        envVarsMap.put("REPMGR_RECONNECT_ATTEMPTS", "1");
        envVarsMap.put("REPMGR_RECONNECT_INTERVAL", "1");
        return envVarsMap;
    }

    @Nonnull
    private PostgresBitnamiRepmgrContainer createContainerAndInitWith(
            final Supplier<Map<String, String>> envVarsProvider,
            final String alias,
            final WaitStrategy waitStrategy
    ) {
        //noinspection resource
        return new PostgresBitnamiRepmgrContainer(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_TAG), envVarsProvider.get())
                .withCreateContainerCmdModifier(cmd -> cmd.withName(alias))
                .withSharedMemorySize(MemoryUnit.MB.convertToBytes(768))
                .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
                .withNetwork(network)
                .withNetworkAliases(alias)
                .withExposedPorts(5432)
                .waitingFor(waitStrategy);
    }

    @Nonnull
    private static String preparePostgresBitnamiVersion() {
        // Bitnami images use semantic versioning with three digits
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion + ".0";
        }
        return "15.1.0";
    }

    private void throwErrorIfNotInitialized() {
        if (containerForPrimary == null || dataSourceForPrimary == null || containerForStandBy == null || dataSourceForStandBy == null) {
            throw new AssertionError("not initialized");
        }
    }
}
