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
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

final class PostgreSqlContainerWrapper implements AutoCloseable {

    private final PostgreSQLContainer<?> container;
    private final BasicDataSource dataSource;

    PostgreSqlContainerWrapper(@Nonnull final List<Pair<String, String>> additionalParameters) {
        final String pgVersion = preparePostgresVersion();
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
        return Optional.ofNullable(System.getenv("TEST_PG_VERSION"))
                .orElse("14.5");
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
}
