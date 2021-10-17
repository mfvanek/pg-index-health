/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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
import org.testcontainers.containers.PostgreSQLContainerProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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
public class PostgresDbExtension implements BeforeAllCallback, AfterAllCallback {

    private volatile JdbcDatabaseContainer<?> container;
    private volatile DataSource dataSource;
    private final List<Pair<String, String>> additionalParameters = new ArrayList<>();

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        final String pgVersion = preparePostgresVersion();
        container = new PostgreSQLContainerProvider().newInstance(pgVersion);
        final String[] startupCommand = prepareStartupCommand(container, additionalParameters);
        container.setCommand(startupCommand);
        container.start();

        dataSource = getDataSource();
    }

    @Nonnull
    private static String preparePostgresVersion() {
        return Optional.ofNullable(System.getenv("TEST_PG_VERSION")).orElse("14.0");
    }

    @Nonnull
    private static String[] prepareStartupCommand(@Nonnull JdbcDatabaseContainer<?> container,
                                                  @Nonnull List<Pair<String, String>> additionalParameters) {
        return Stream.concat(
                Arrays.stream(container.getCommandParts()),
                additionalParameters.stream()
                        .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
        ).toArray(String[]::new);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        additionalParameters.clear();
        container.close();
        dataSource = null;
        container = null;
    }

    @Nonnull
    public DataSource getTestDatabase() {
        throwErrorIfNotInitialized();
        return dataSource;
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

    public PostgresDbExtension withAdditionalStartupParameter(String key, String value) {
        additionalParameters.add(Pair.of(key, value));
        return this;
    }

    private void throwErrorIfNotInitialized() {
        if (container == null || dataSource == null) {
            throw new AssertionError("not initialized");
        }
    }

    @Nonnull
    private BasicDataSource getDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }
}
