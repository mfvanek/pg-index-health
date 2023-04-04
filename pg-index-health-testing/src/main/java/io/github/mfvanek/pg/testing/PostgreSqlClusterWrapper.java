/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.testing.annotations.ExcludeFromJacocoGeneratedReport;
import org.apache.commons.dbcp2.BasicDataSource;
import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
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
public final class PostgreSqlClusterWrapper implements AutoCloseable {

    public static final Duration WAIT_INTERVAL_SECONDS = Duration.ofSeconds(100L);
    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlClusterWrapper.class);

    private final PostgreSqlClusterAliasHolder aliases;
    private final PostgresVersionHolder pgVersion;
    private final Network network;
    private final JdbcDatabaseContainer<?> containerForPrimary;
    private final JdbcDatabaseContainer<?> containerForStandBy;
    private final BasicDataSource dataSourceForPrimary;
    private final BasicDataSource dataSourceForStandBy;
    private final String username;
    private final String password;

    private PostgreSqlClusterWrapper(final Builder builder) {
        this.pgVersion = PostgresVersionHolder.forCluster();
        this.network = Network.newNetwork();

        this.username = builder.username;
        this.password = builder.password;

        this.aliases = new PostgreSqlClusterAliasHolder();
        // Primary node
        this.containerForPrimary = createContainerAndInitWith(
                () -> aliases.createPrimaryEnvVarsMap(username, password),
                aliases.getPrimaryAlias(),
                aliases.getWaitStrategyForPrimary());
        // Standby node
        this.containerForStandBy = createContainerAndInitWith(
                () -> aliases.createStandbyEnvVarsMap(username, password),
                aliases.getStandbyAlias(),
                aliases.getWaitStrategyForStandBy()
        );

        this.containerForPrimary.start();
        Awaitility.await("Ensure primary is ready")
                .atMost(PostgreSqlClusterAliasHolder.STARTUP_TIMEOUT)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForPrimary.getLogs().contains("database system is ready to accept connections"));
        this.containerForStandBy.start();
        Awaitility.await("Ensure cluster is ready")
                .atMost(PostgreSqlClusterAliasHolder.STARTUP_TIMEOUT)
                .pollInterval(Duration.ofSeconds(1L))
                .until(() -> containerForStandBy.getLogs().contains("started streaming WAL from primary"));

        this.dataSourceForPrimary = PostgreSqlDataSourceHelper.buildDataSource(containerForPrimary);
        this.dataSourceForStandBy = PostgreSqlDataSourceHelper.buildDataSource(containerForStandBy);
    }

    /**
     * {@inheritDoc}
     */
    @ExcludeFromJacocoGeneratedReport
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
        return containerForPrimary.getJdbcUrl();
    }

    @Nonnull
    public String getSecondContainerJdbcUrl() {
        throwErrorIfNotInitialized();
        return containerForStandBy.getJdbcUrl();
    }

    @Nonnull
    public String getUsername() {
        return containerForPrimary.getUsername();
    }

    @Nonnull
    public String getPassword() {
        return containerForPrimary.getPassword();
    }

    public boolean stopFirstContainer() {
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
        return true;
    }

    @Nonnull
    private PostgresBitnamiRepmgrContainer createContainerAndInitWith(
            final Supplier<Map<String, String>> envVarsProvider,
            final String alias,
            final WaitStrategy waitStrategy
    ) {
        final DockerImageName dockerImageName = DockerImageName.parse(IMAGE_NAME)
                .withTag(pgVersion.getVersion());
        //noinspection resource
        return new PostgresBitnamiRepmgrContainer(dockerImageName, envVarsProvider.get()) //NOSONAR
                .withCreateContainerCmdModifier(cmd -> cmd.withName(alias))
                .withSharedMemorySize(MemoryUnit.MB.convertToBytes(768))
                .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
                .withNetwork(network)
                .withNetworkAliases(alias)
                .withExposedPorts(5432)
                .waitingFor(waitStrategy);
    }

    @ExcludeFromJacocoGeneratedReport
    private void throwErrorIfNotInitialized() {
        if (containerForPrimary == null || dataSourceForPrimary == null || containerForStandBy == null || dataSourceForStandBy == null) {
            throw new AssertionError("not initialized");
        }
    }

    /**
     * Provide convenient way to create cluster with single username/password.
     * If no username/password is specified, "customuser" and "custompassword" will be used as default values for username and password, respectively
     */
    public static class Builder {

        private String username;
        private String password;

        public Builder() {
            this.username = "customuser";
            this.password = "custompassword";
        }

        public Builder username(final String username) {
            this.username = username;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public PostgreSqlClusterWrapper build() {
            return new PostgreSqlClusterWrapper(this);
        }
    }
}
