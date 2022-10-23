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
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

final class PostgreSqlContainerWrapper implements AutoCloseable {

    private final String pgVersion;
    private final PostgreSQLContainer<?> container;
    private final BasicDataSource dataSource;

    PostgreSqlContainerWrapper(@Nonnull final List<Pair<String, String>> additionalParameters) {
        this.pgVersion = preparePostgresVersion();
        //noinspection resource
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag(pgVersion))
                .withSharedMemorySize(MemoryUnit.MB.convertToBytes(512))
                .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"))
                .withCommand(prepareCommandParts(additionalParameters));
        this.container.start();
        this.dataSource = PostgreSqlDataSourceHelper.buildDataSource(container);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        try {
            dataSource.close();
        } catch (SQLException ignored) {
            // ignore
        }
        container.close();
    }

    @Nonnull
    private static String preparePostgresVersion() {
        final String pgVersion = System.getenv("TEST_PG_VERSION");
        if (pgVersion != null) {
            return pgVersion;
        }
        return "15.0";
    }

    @Nonnull
    private static String[] prepareCommandParts(@Nonnull final List<Pair<String, String>> additionalParameters) {
        return additionalParameters.stream()
                .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
                .toArray(String[]::new);
    }

    @Nonnull
    public DataSource getDataSource() {
        return dataSource;
    }

    public int getPort() {
        return container.getFirstMappedPort();
    }

    @Nonnull
    public String getUrl() {
        return container.getJdbcUrl();
    }

    @Nonnull
    public String getUsername() {
        return container.getUsername();
    }

    @Nonnull
    public String getPassword() {
        return container.getPassword();
    }

    /**
     * Checks whether <a href="https://www.postgresql.org/docs/current/monitoring-stats.html">The Cumulative Statistics System</a> is supported for given PostgreSQL container.
     *
     * @return true for version 15 and higher
     * @see <a href="https://www.percona.com/blog/postgresql-15-stats-collector-gone-whats-new/">PostgreSQL 15: Stats Collector Gone? What’s New?</a>
     * @since 0.7.0
     */
    public boolean isCumulativeStatisticsSystemSupported() {
        return getMajorVersion() >= 15;
    }

    /**
     * Checks whether <a href="https://www.postgresql.org/docs/current/sql-createprocedure.html">CREATE PROCEDURE</a> command is supported for given PostgreSQL container.
     *
     * @return true for version 11 and higher
     * @since 0.7.0
     */
    public boolean isProceduresSupported() {
        return getMajorVersion() >= 11;
    }

    private int getMajorVersion() {
        final String[] parts = pgVersion.split("\\.");
        return Integer.parseInt(parts[0]);
    }
}
