/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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

    /**
     * Represents a fixed wait interval of 100 seconds, typically used for polling or retry mechanisms within
     * the PostgreSQL cluster environment.
     */
    public static final Duration WAIT_INTERVAL_SECONDS = Duration.ofSeconds(100L);
    private static final Logger LOGGER = Logger.getLogger(PostgreSqlClusterWrapper.class.getName());

    private final String postgresVersion;
    private final String dockerImageName;
    private final Network network;
    private final JdbcDatabaseContainer<PostgresBitnamiRepmgrContainer> containerForPrimary;
    private final JdbcDatabaseContainer<PostgresBitnamiRepmgrContainer> containerForStandBy;
    private final BasicDataSource dataSourceForPrimary;
    private final BasicDataSource dataSourceForStandBy;

    private PostgreSqlClusterWrapper(final PostgreSqlClusterBuilder builder) {
        this.postgresVersion = builder.postgresVersion;
        this.dockerImageName = builder.dockerImageName;
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

    /**
     * Retrieves the {@link DataSource} for the primary PostgreSQL node in the cluster.
     *
     * @return the {@link DataSource} for the primary container
     */
    public DataSource getDataSourceForPrimary() {
        return dataSourceForPrimary;
    }

    /**
     * Retrieves the {@link DataSource} for the standby PostgreSQL node in the cluster.
     *
     * @return the {@link DataSource} for the standby container
     */
    public DataSource getDataSourceForStandBy() {
        return dataSourceForStandBy;
    }

    /**
     * Retrieves the JDBC URL for the first (usually primary) PostgreSQL node in the cluster.
     *
     * @return the JDBC URL of the primary container as a string
     */
    public String getFirstContainerJdbcUrl() {
        return containerForPrimary.getJdbcUrl();
    }

    /**
     * Retrieves the JDBC URL for the second (usually standby) PostgreSQL node in the cluster.
     *
     * @return the JDBC URL of the standby container as a string
     */
    public String getSecondContainerJdbcUrl() {
        return containerForStandBy.getJdbcUrl();
    }

    /**
     * Builds and retrieves a common JDBC URL suitable for connecting to the primary PostgreSQL node in the cluster.
     *
     * @return a string representing the common JDBC URL for the primary PostgreSQL node
     */
    public String getCommonUrlToPrimary() {
        return PgUrlParser.buildCommonUrlToPrimary(containerForPrimary.getJdbcUrl(), containerForStandBy.getJdbcUrl());
    }

    /**
     * Retrieves the username used by the PostgreSQL cluster.
     *
     * @return the username as a string
     */
    public String getUsername() {
        return containerForPrimary.getUsername();
    }

    /**
     * Retrieves the password used by the PostgreSQL cluster.
     *
     * @return the password as a string
     */
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
        final DockerImageName dockerImage = DockerImageName.parse(dockerImageName)
            .withTag(postgresVersion);
        //noinspection resource
        return new PostgresBitnamiRepmgrContainer(dockerImage, envVars) //NOSONAR
            .withCreateContainerCmdModifier(cmd -> cmd.withName(alias))
            .withSharedMemorySize(MemoryUnit.MB.convertToBytes(768))
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
            .withNetwork(network)
            .withNetworkAliases(alias)
            .withExposedPorts(5432)
            .waitingFor(waitStrategy);
    }

    /**
     * Creates and returns an instance of {@link PostgreSqlClusterBuilder} to facilitate the construction
     * of a PostgreSQL cluster with customizable parameters such as username, password, database name,
     * and PostgreSQL version.
     *
     * @return a new instance of {@link PostgreSqlClusterBuilder} for configuring and building a PostgreSQL cluster
     */
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
        private String postgresVersion = "17.6.0";
        private String dockerImageName = "docker.io/bitnamilegacy/postgresql-repmgr";

        private PostgreSqlClusterBuilder() {
        }

        /**
         * Retrieves the username set for the PostgreSQL cluster.
         *
         * @return the username as a string
         */
        public String getUsername() {
            return username;
        }

        /**
         * Retrieves the password set for the PostgreSQL cluster.
         *
         * @return the password as a string
         */
        public String getPassword() {
            return password;
        }

        /**
         * Retrieves the name of the database set for the PostgreSQL cluster.
         *
         * @return the name of the database as a string
         */
        public String getDatabaseName() {
            return databaseName;
        }

        /**
         * Sets the username to be used for the PostgreSQL cluster.
         *
         * @param username the username to set; must not be null
         * @return the current {@code PostgreSqlClusterBuilder} instance with the specified username applied
         */
        public PostgreSqlClusterBuilder withUsername(final String username) {
            this.username = Objects.requireNonNull(username, "username cannot be null");
            return this;
        }

        /**
         * Sets the password to be used for the PostgreSQL cluster.
         *
         * @param password the password to set; must not be null
         * @return the current {@code PostgreSqlClusterBuilder} instance with the specified password applied
         */
        public PostgreSqlClusterBuilder withPassword(final String password) {
            this.password = Objects.requireNonNull(password, "password cannot be null");
            return this;
        }

        /**
         * Sets the name of the database to be used in the PostgreSQL cluster.
         *
         * @param databaseName the name of the database; must not be null
         * @return the current {@code PostgreSqlClusterBuilder} instance with the specified database name applied
         */
        public PostgreSqlClusterBuilder withDatabaseName(final String databaseName) {
            this.databaseName = Objects.requireNonNull(databaseName, "databaseName cannot be null");
            return this;
        }

        /**
         * Sets the PostgreSQL version for the cluster.
         *
         * @param postgresVersion the PostgreSQL version to be used; must not be null
         * @return the current {@code PostgreSqlClusterBuilder} instance with the specified version applied
         */
        public PostgreSqlClusterBuilder withPostgresVersion(final String postgresVersion) {
            this.postgresVersion = Objects.requireNonNull(postgresVersion, "postgresVersion cannot be null");
            return this;
        }

        /**
         * Sets the Docker image name to be used for the PostgreSQL cluster.
         *
         * @param dockerImageName the name of the Docker image; must not be null
         * @return the current {@code PostgreSqlClusterBuilder} instance with the specified Docker image name applied
         */
        public PostgreSqlClusterBuilder withDockerImageName(final String dockerImageName) {
            this.dockerImageName = Objects.requireNonNull(dockerImageName, "dockerImageName cannot be null");
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
