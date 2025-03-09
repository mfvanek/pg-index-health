/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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

    public static final Integer POSTGRESQL_PORT = 5432;
    private final Map<String, String> envVars;

    PostgresBitnamiRepmgrContainer(final DockerImageName dockerImageName, final Map<String, String> envVars) {
        super(dockerImageName);
        this.envVars = Map.copyOf(envVars);
        addExposedPort(POSTGRESQL_PORT);
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return PgUrlParser.URL_HEADER +
            getHost() +
            ":" +
            getMappedPort(POSTGRESQL_PORT) +
            "/" +
            envVars.get("POSTGRESQL_DATABASE") +
            additionalUrlParams;
    }

    @Override
    public String getUsername() {
        return envVars.get("POSTGRESQL_USERNAME");
    }

    @Override
    public String getPassword() {
        return envVars.get("POSTGRESQL_PASSWORD");
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    protected void configure() {
        super.configure();
        envVars.forEach(this::addEnv);
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("PMD.CloseResource")
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof PostgresBitnamiRepmgrContainer)) {
            return false;
        }

        final PostgresBitnamiRepmgrContainer that = (PostgresBitnamiRepmgrContainer) other;
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
