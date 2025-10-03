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
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;
import java.util.Objects;

/**
 * Bitnami container with repmgr. Should be configured via environment variables as said in bitnami image documentation
 * <a href="https://github.com/bitnami/containers/tree/main/bitnami/postgresql-repmgr#setting-up-a-ha-postgresql-cluster-with-streaming-replication-and-repmgr">here</a>.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
class PostgresBitnamiRepmgrContainer extends JdbcDatabaseContainer<PostgresBitnamiRepmgrContainer> {

    /**
     * The default port number used by PostgreSQL database instances.
     */
    public static final Integer POSTGRESQL_PORT = 5432;
    private final Map<String, String> envVars;

    /**
     * Constructs a new instance of {@code PostgresBitnamiRepmgrContainer}.
     * This container is designed for use with the Bitnami PostgreSQL Replication Manager (repmgr) image.
     *
     * @param dockerImageName the name of the Docker image to be used for this container
     * @param envVars         a map of environment variables to configure the container
     */
    PostgresBitnamiRepmgrContainer(final DockerImageName dockerImageName, final Map<String, String> envVars) {
        super(dockerImageName);
        this.envVars = Map.copyOf(envVars);
        addExposedPort(POSTGRESQL_PORT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return PgUrlParser.URL_HEADER +
            getHost() +
            ":" +
            getMappedPort(POSTGRESQL_PORT) +
            "/" +
            envVars.getOrDefault("POSTGRESQL_DATABASE", "postgres") +
            additionalUrlParams;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUsername() {
        return envVars.getOrDefault("POSTGRESQL_USERNAME", "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPassword() {
        return envVars.getOrDefault("POSTGRESQL_PASSWORD", "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configure() {
        super.configure();
        envVars.forEach(this::addEnv);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final PostgresBitnamiRepmgrContainer that)) {
            return false;
        }

        return Objects.equals(envVars, that.envVars);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(this), envVars);
    }
}
