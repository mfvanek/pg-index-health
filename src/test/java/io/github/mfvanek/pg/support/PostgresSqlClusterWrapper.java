/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.model.MemoryUnit;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
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
final class PostgresSqlClusterWrapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresSqlClusterWrapper.class);
    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final String IMAGE_TAG = preparePostgresBitnamiVersion();
    private static final String PRIMARY_ALIAS;
    private static final String STANDBY_ALIAS;

    static {
        // REPMGR_NODE_NAME must end with a number, so aliases must also
        // To avoid a ConflictException when starting the container, aliases must be unique if there is more than one instance of PostgresSqlClusterWrapper
        final UUID uuid = UUID.randomUUID();
        PRIMARY_ALIAS = String.format("pg-%s-0", uuid);
        STANDBY_ALIAS = String.format("pg-%s-1", uuid);
    }

    private final Network network;
    private final JdbcDatabaseContainer<?> containerForPrimary;
    private final JdbcDatabaseContainer<?> containerForStandBy;

    private final DataSource dataSourceForPrimary;
    private final DataSource dataSourceForStandBy;

    PostgresSqlClusterWrapper() {
        this.network = Network.newNetwork();
        // Primary node
        final WaitStrategy waitStrategyForPrimary = new LogMessageWaitStrategy()
                .withRegEx(".*Starting repmgrd.*\\s")
                .withStartupTimeout(Duration.ofSeconds(30));
        this.containerForPrimary = createContainerAndInitWith(this::primaryEnvVarsMap, PRIMARY_ALIAS, waitStrategyForPrimary);
        // Standby node
        final WaitStrategy waitStrategyForStandBy = new LogMessageWaitStrategy()
                .withRegEx(".*starting monitoring of node.*\\s")
                .withStartupTimeout(Duration.ofSeconds(30));
        this.containerForStandBy = createContainerAndInitWith(this::standbyEnvVarsMap, STANDBY_ALIAS, waitStrategyForStandBy);

        this.containerForPrimary.start();
        this.containerForStandBy.start();

        this.dataSourceForPrimary = PostgreSqlDataSourceHelper.buildDataSource(containerForPrimary);
        this.dataSourceForStandBy = PostgreSqlDataSourceHelper.buildDataSource(containerForStandBy);
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
        return String.format("jdbc:postgresql://%s:%d/%s", PRIMARY_ALIAS, containerForPrimary.getFirstMappedPort(), containerForPrimary.getDatabaseName());
    }

    @Nonnull
    public String getSecondContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://%s:%d/%s", STANDBY_ALIAS, containerForStandBy.getFirstMappedPort(), containerForStandBy.getDatabaseName());
    }

    public void stopFirstContainer() {
        containerForPrimary.stop();
        LOGGER.info("Waiting for standby will be promoted to primary");
        Awaitility.await("Promoting standby to primary")
                .atMost(Duration.ofSeconds(100L))
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("promoting standby to primary"));
        Awaitility.await("Standby promoted to primary")
                .atMost(Duration.ofSeconds(100L))
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("standby promoted to primary after"));
    }

    public void startFirstContainer() {
        LOGGER.info("Starting first container");
        containerForPrimary.start();
    }

    @Nonnull
    private Map<String, String> primaryEnvVarsMap() {
        final Map<String, String> envVarsMap = new HashMap<>();
        envVarsMap.put("POSTGRESQL_POSTGRES_PASSWORD", "adminpassword");
        envVarsMap.put("POSTGRESQL_USERNAME", "customuser");
        envVarsMap.put("POSTGRESQL_PASSWORD", "custompassword");
        envVarsMap.put("POSTGRESQL_DATABASE", "customdatabase");
        envVarsMap.put("REPMGR_PASSWORD", "repmgrpassword");
        envVarsMap.put("REPMGR_PRIMARY_HOST", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", PRIMARY_ALIAS, STANDBY_ALIAS));
        envVarsMap.put("REPMGR_NODE_NAME", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", PRIMARY_ALIAS);
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
        envVarsMap.put("REPMGR_PRIMARY_HOST", PRIMARY_ALIAS);
        envVarsMap.put("REPMGR_PRIMARY_PORT", "5432");
        envVarsMap.put("REPMGR_PARTNER_NODES", String.format("%s,%s:5432", PRIMARY_ALIAS, STANDBY_ALIAS));
        envVarsMap.put("REPMGR_NODE_NAME", STANDBY_ALIAS);
        envVarsMap.put("REPMGR_NODE_NETWORK_NAME", STANDBY_ALIAS);
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
        final PostgresBitnamiRepmgrContainer container = new PostgresBitnamiRepmgrContainer(DockerImageName.parse(IMAGE_NAME).withTag(IMAGE_TAG), envVarsProvider.get());
        container.withCreateContainerCmdModifier(cmd -> cmd.withName(alias));
        container.setNetwork(network);
        container.setWaitStrategy(waitStrategy);
        container.setExposedPorts(Collections.singletonList(5432));
        container.setNetworkAliases(Collections.singletonList(alias));
        container.withSharedMemorySize(MemoryUnit.MB.convertToBytes(512));
        container.withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));
        return container;
    }

    @Nonnull
    private static String preparePostgresBitnamiVersion() {
        // Bitnami images use semantic versioning with three digits
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion + ".0";
        }
        return "14.5.0";
    }

    private void throwErrorIfNotInitialized() {
        if (containerForPrimary == null || dataSourceForPrimary == null || containerForStandBy == null || dataSourceForStandBy == null) {
            throw new AssertionError("not initialized");
        }
    }
}
