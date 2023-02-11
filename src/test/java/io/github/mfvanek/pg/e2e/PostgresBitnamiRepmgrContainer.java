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

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
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
        this.envVars = Collections.unmodifiableMap(envVars);
        addExposedPort(POSTGRESQL_PORT);
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getJdbcUrl() {
        final String additionalUrlParams = constructUrlParameters("?", "&");
        return "jdbc:postgresql://" +
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
    public String getDatabaseName() {
        return envVars.get("POSTGRESQL_DATABASE");
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
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        try (PostgresBitnamiRepmgrContainer that = (PostgresBitnamiRepmgrContainer) o) {
            return Objects.equals(envVars, that.envVars);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), envVars);
    }
}
