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
import io.github.mfvanek.pg.testing.annotations.ExcludeFromJacocoGeneratedReport;
import org.apache.commons.dbcp2.BasicDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public final class PostgreSqlContainerWrapper implements AutoCloseable, PostgresVersionAware {

    private final PostgresVersionHolder pgVersion;
    private final PostgreSQLContainer<?> container;
    private final BasicDataSource dataSource;

    PostgreSqlContainerWrapper(@Nonnull final PostgresVersionHolder pgVersion,
                               @Nonnull final List<Map.Entry<String, String>> additionalParameters) {
        this.pgVersion = Objects.requireNonNull(pgVersion, "pgVersion cannot be null");
        //noinspection resource
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres") //NOSONAR
                .withTag(pgVersion.getVersion()))
                .withSharedMemorySize(MemoryUnit.MB.convertToBytes(512))
                .withTmpFs(Map.of("/var/lib/postgresql/data", "rw"))
                .withCommand(prepareCommandParts(additionalParameters));
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

    @Nonnull
    public static PostgreSqlContainerWrapper withDefaultVersion() {
        return new PostgreSqlContainerWrapper(PostgresVersionHolder.forSingleNode());
    }

    @Nonnull
    public static PostgreSqlContainerWrapper withVersion(@Nonnull final PostgresVersionHolder pgVersion) {
        return new PostgreSqlContainerWrapper(pgVersion);
    }
}
