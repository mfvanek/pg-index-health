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

import io.github.mfvanek.pg.model.settings.ImportantParam;
import io.github.mfvanek.pg.model.units.MemoryUnit;
import io.github.mfvanek.pg.testing.annotations.ExcludeFromJacocoGeneratedReport;
import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.sql.DataSource;

public final class PostgreSqlContainerWrapper implements AutoCloseable, PostgresVersionAware {

    private final PostgresVersionHolder pgVersion;
    private final PostgreSQLContainer<?> container;
    private final BasicDataSource dataSource;

    PostgreSqlContainerWrapper(final PostgresVersionHolder pgVersion,
                               final List<Map.Entry<String, String>> additionalParameters) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
        //noinspection resource
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres") //NOSONAR
            .withTag(pgVersion.getVersion()))
            .withSharedMemorySize(MemoryUnit.MB.convertToBytes(512))
            .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
            .withCommand(prepareCommandParts(additionalParameters))
            .waitingFor(Wait.defaultWaitStrategy());
        this.container.start();
        this.dataSource = PostgreSqlDataSourceHelper.buildDataSource(container);
    }

    PostgreSqlContainerWrapper(final PostgresVersionHolder pgVersion) {
        this(pgVersion, List.of(
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
    @ExcludeFromJacocoGeneratedReport
    @Override
    public void close() {
        try {
            dataSource.close();
        } catch (SQLException ignored) {
            // ignore
        }
        container.close();
    }

    private static String[] prepareCommandParts(final Collection<Map.Entry<String, String>> additionalParameters) {
        return additionalParameters.stream()
            .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
            .toArray(String[]::new);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public int getPort() {
        return container.getFirstMappedPort();
    }

    public String getUrl() {
        return container.getJdbcUrl();
    }

    public String getUsername() {
        return container.getUsername();
    }

    public String getPassword() {
        return container.getPassword();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCumulativeStatisticsSystemSupported() {
        return pgVersion.isCumulativeStatisticsSystemSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isProceduresSupported() {
        return pgVersion.isProceduresSupported();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isOutParametersInProcedureSupported() {
        return pgVersion.isOutParametersInProcedureSupported();
    }

    /**
     * Creates {@code PostgreSqlContainerWrapper} with default PostgreSQL version.
     * The default version is taken from the environment variable {@code TEST_PG_VERSION} if it is set,
     * otherwise the default version {@code 17.4} is used.
     *
     * @return {@code PostgreSqlContainerWrapper}
     */
    public static PostgreSqlContainerWrapper withDefaultVersion() {
        return new PostgreSqlContainerWrapper(PostgresVersionHolder.forSingleNode());
    }

    /**
     * Creates {@code PostgreSqlContainerWrapper} with given version.
     *
     * @param pgVersion given PostgreSQL version
     * @return {@code PostgreSqlContainerWrapper}
     */
    public static PostgreSqlContainerWrapper withVersion(final String pgVersion) {
        return new PostgreSqlContainerWrapper(PostgresVersionHolder.forSingleNode(pgVersion));
    }
}
