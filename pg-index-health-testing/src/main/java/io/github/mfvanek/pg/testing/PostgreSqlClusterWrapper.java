/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import io.github.mfvanek.pg.connection.host.PgUrlParser;
import io.github.mfvanek.pg.model.annotations.ExcludeFromJacocoGeneratedReport;
import io.github.mfvanek.pg.model.units.MemoryUnit;
import org.apache.commons.dbcp2.BasicDataSource;
import org.awaitility.Awaitility;
import org.jspecify.annotations.Nullable;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * This wrapper provides postgres containers as part of the HA cluster with repmgr configured.
 * <p>
 * If master goes down, repmgr will ensure any of the standby nodes takes the primary role.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
public final class PostgreSqlClusterWrapper implements AutoCloseable {

    public static final Duration WAIT_INTERVAL_SECONDS = Duration.ofSeconds(100L);
    private static final String IMAGE_NAME = "docker.io/bitnami/postgresql-repmgr";
    private static final Logger LOGGER = Logger.getLogger(PostgreSqlClusterWrapper.class.getName());

    private final PostgresVersionHolder pgVersion;
    private final Network network;
    private final JdbcDatabaseContainer<PostgresBitnamiRepmgrContainer> containerForPrimary;
    private final JdbcDatabaseContainer<PostgresBitnamiRepmgrContainer> containerForStandBy;
    private final BasicDataSource dataSourceForPrimary;
    private final BasicDataSource dataSourceForStandBy;

    private PostgreSqlClusterWrapper(final PostgreSqlClusterBuilder builder) {
        final String version = builder.getPostgresVersion();
        this.pgVersion = version != null ? PostgresVersionHolder.forCluster(version) : PostgresVersionHolder.forCluster();
        this.network = Network.newNetwork();

        final PostgreSqlClusterAliasHolder aliases = new PostgreSqlClusterAliasHolder();
        // Primary node
        this.containerForPrimary = createContainerAndInitWith(
            aliases.createPrimaryEnvVarsMap(builder),
            aliases.getPrimaryAlias(),
            aliases.getWaitStrategyForPrimary());
        // Standby node
        this.containerForStandBy = createContainerAndInitWith(
            aliases.createStandbyEnvVarsMap(builder),
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
            LOGGER.log(Level.WARNING, "Error occurred while closing data source to replica", ex);
        }
        try {
            dataSourceForPrimary.close();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Error occurred while closing data source to primary", ex);
        }
        containerForStandBy.close();
        containerForPrimary.close();
    }

    public DataSource getDataSourceForPrimary() {
        return dataSourceForPrimary;
    }

    public DataSource getDataSourceForStandBy() {
        return dataSourceForStandBy;
    }

    public String getFirstContainerJdbcUrl() {
        return containerForPrimary.getJdbcUrl();
    }

    public String getSecondContainerJdbcUrl() {
        return containerForStandBy.getJdbcUrl();
    }

    public String getCommonUrlToPrimary() {
        return PgUrlParser.buildCommonUrlToPrimary(containerForPrimary.getJdbcUrl(), containerForStandBy.getJdbcUrl());
    }

    public String getUsername() {
        return containerForPrimary.getUsername();
    }

    public String getPassword() {
        return containerForPrimary.getPassword();
    }

    /**
     * Stops the first container in the cluster and waits for auto failover.
     *
     * @return always true
     */
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

    private PostgresBitnamiRepmgrContainer createContainerAndInitWith(
        final Map<String, String> envVars,
        final String alias,
        final WaitStrategy waitStrategy
    ) {
        final DockerImageName dockerImageName = DockerImageName.parse(IMAGE_NAME)
            .withTag(pgVersion.getVersion());
        //noinspection resource
        return new PostgresBitnamiRepmgrContainer(dockerImageName, envVars) //NOSONAR
            .withCreateContainerCmdModifier(cmd -> cmd.withName(alias))
            .withSharedMemorySize(MemoryUnit.MB.convertToBytes(768))
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
            .withNetwork(network)
            .withNetworkAliases(alias)
            .withExposedPorts(5432)
            .waitingFor(waitStrategy);
    }

    public static PostgreSqlClusterBuilder builder() {
        return new PostgreSqlClusterBuilder();
    }

    /**
     * Provide convenient way to create cluster with single username/password.
     * If no username/password is specified, "customuser" and "custompassword" will be used as default values for username and password, respectively.
     *
     * @author Alexey Antipin
     */
    public static class PostgreSqlClusterBuilder {

        private String username = "customuser";
        private String password = "custompassword";
        private String databaseName = "customdatabase";
        private @Nullable String postgresVersion;

        private PostgreSqlClusterBuilder() {
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        @Nullable
        public String getPostgresVersion() {
            return postgresVersion;
        }

        public PostgreSqlClusterBuilder withUsername(final String username) {
            this.username = Objects.requireNonNull(username, "username cannot be null");
            return this;
        }

        public PostgreSqlClusterBuilder withPassword(final String password) {
            this.password = Objects.requireNonNull(password, "password cannot be null");
            return this;
        }

        public PostgreSqlClusterBuilder withDatabaseName(final String databaseName) {
            this.databaseName = Objects.requireNonNull(databaseName, "databaseName cannot be null");
            return this;
        }

        public PostgreSqlClusterBuilder withPostgresVersion(final String postgresVersion) {
            this.postgresVersion = Objects.requireNonNull(postgresVersion, "postgresVersion cannot be null");
            return this;
        }

        /**
         * Creates a PostgresSqlClusterWrapper with given parameters.
         *
         * @return PostgreSqlClusterWrapper
         */
        public PostgreSqlClusterWrapper build() {
            return new PostgreSqlClusterWrapper(this);
        }
    }
}
