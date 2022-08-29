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

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class PostgreSqlContainerWrapper {

    private final PostgreSQLContainer<?> container;
    private final DataSource dataSource;

    public PostgreSqlContainerWrapper(@Nonnull final String pgVersion) {
        this(pgVersion, Collections.emptyList());
    }

    public PostgreSqlContainerWrapper(@Nonnull final List<Pair<String, String>> additionalParameters) {
        this(preparePostgresVersion(), additionalParameters);
    }

    public PostgreSqlContainerWrapper(@Nonnull final String pgVersion, @Nonnull final List<Pair<String, String>> additionalParameters) {
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag(pgVersion))
                .withSharedMemorySize(512L * 1024L * 1024L)
                .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"))
                .withCommand(prepareCommandParts(additionalParameters));
        this.container.start();
        this.dataSource = buildDataSource();
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

    @Nonnull
    private DataSource buildDataSource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }
}
