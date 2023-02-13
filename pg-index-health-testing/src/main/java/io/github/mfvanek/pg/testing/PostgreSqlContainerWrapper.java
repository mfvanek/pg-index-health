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
import io.github.mfvanek.pg.settings.ImportantParam;
import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public final class PostgreSqlContainerWrapper implements AutoCloseable {

    private final String pgVersion;
    private final PostgreSQLContainer<?> container;
    private final BasicDataSource dataSource;

    public PostgreSqlContainerWrapper(@Nonnull final List<Map.Entry<String, String>> additionalParameters) {
        this.pgVersion = preparePostgresVersion();
        //noinspection resource
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres") //NOSONAR
                .withTag(pgVersion))
                .withSharedMemorySize(MemoryUnit.MB.convertToBytes(512))
                .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
                .withCommand(prepareCommandParts(additionalParameters));
        this.container.start();
        this.dataSource = PostgreSqlDataSourceHelper.buildDataSource(container);
    }

    public PostgreSqlContainerWrapper() {
        this(List.of(
                Map.entry(ImportantParam.LOCK_TIMEOUT.getName(), "1000"),
                Map.entry(ImportantParam.SHARED_BUFFERS.getName(), "256MB"),
                Map.entry(ImportantParam.MAINTENANCE_WORK_MEM.getName(), "128MB"),
                Map.entry(ImportantParam.WORK_MEM.getName(), "16MB"),
                Map.entry(ImportantParam.RANDOM_PAGE_COST.getName(), "1")
        ));
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
        return "15.2";
    }

    @Nonnull
    private static String[] prepareCommandParts(@Nonnull final List<Map.Entry<String, String>> additionalParameters) {
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

    /**
     * Checks whether <a href="https://www.postgresql.org/docs/current/sql-createprocedure.html">CREATE PROCEDURE</a> command supports OUT parameters.
     *
     * @return true for version 14 and higher
     * @since 0.7.0
     */
    public boolean isOutParametersInProcedureSupported() {
        return isProceduresSupported() && getMajorVersion() >= 14;
    }
}
