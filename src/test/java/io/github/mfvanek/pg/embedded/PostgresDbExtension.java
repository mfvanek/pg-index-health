/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.embedded;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * JUnit test extension that provides configurable PostgreSQL instance.
 * Additional layer of abstraction that allow to easily switch between
 * embedded PostgreSQL implementations (otj-opentable, zonkyio, testcontainers).
 *
 * @author Nikolay Kondratyev
 */
@SuppressWarnings("PMD.AvoidUsingVolatile")
public class PostgresDbExtension implements BeforeAllCallback, AfterAllCallback {

    private final JdbcDatabaseContainer<?> container;
    private final AtomicReference<DataSource> dataSourceRef = new AtomicReference<>();

    public PostgresDbExtension(@Nonnull final List<Pair<String, String>> additionalParameters) {
        final String pgVersion = preparePostgresVersion();
        final String[] commandParts = additionalParameters.stream()
                .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
                .toArray(String[]::new);
        this.container = new PostgreSQLContainer<>(DockerImageName.parse("postgres").withTag(pgVersion))
                .withCommand(commandParts);
    }

    @Override
    public void beforeAll(final ExtensionContext extensionContext) {
        container.start();
        dataSourceRef.set(getDataSource());
    }

    @Nonnull
    private static String preparePostgresVersion() {
        return Optional.ofNullable(System.getenv("TEST_PG_VERSION")).orElse("14.0");
    }

    @Override
    public void afterAll(final ExtensionContext extensionContext) {
        dataSourceRef.set(null);
        container.close();
    }

    @Nonnull
    public DataSource getTestDatabase() {
        throwErrorIfNotInitialized();
        return dataSourceRef.get();
    }

    public int getPort() {
        throwErrorIfNotInitialized();
        return container.getFirstMappedPort();
    }

    @Nonnull
    public String getUrl() {
        throwErrorIfNotInitialized();
        return String.format("jdbc:postgresql://localhost:%d/%s", getPort(), container.getDatabaseName());
    }

    @Nonnull
    public String getUsername() {
        throwErrorIfNotInitialized();
        return container.getUsername();
    }

    @Nonnull
    public String getPassword() {
        throwErrorIfNotInitialized();
        return container.getPassword();
    }

    private void throwErrorIfNotInitialized() {
        if (dataSourceRef.get() == null) {
            throw new AssertionError("not initialized");
        }
    }

    @Nonnull
    private BasicDataSource getDataSource() {
        final BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }
}
