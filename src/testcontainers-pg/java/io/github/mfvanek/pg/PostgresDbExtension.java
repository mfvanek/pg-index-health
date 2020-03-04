/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.PostgreSQLContainerProvider;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class PostgresDbExtension implements BeforeAllCallback, AfterAllCallback {

    private volatile JdbcDatabaseContainer container;
    private volatile DataSource dataSource;

    private volatile List<Pair<String, String>> additionalParameters = new ArrayList<>();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        String pgVersion = Optional.ofNullable(System.getenv("TEST_PG_VERSION")).orElse(PostgreSQLContainer.DEFAULT_TAG);
        container = new PostgreSQLContainerProvider().newInstance(pgVersion);
        String[] startupCommand = Stream.concat(
                Arrays.stream(container.getCommandParts()),
                additionalParameters.stream()
                        .flatMap(kv -> Stream.of("-c", kv.getKey() + "=" + kv.getValue()))
        ).toArray(String[]::new);
        container.setCommand(startupCommand);
        container.start();

        dataSource = getDataSource();
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        container.close();
    }

    @NotNull
    private BasicDataSource getDataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(container.getJdbcUrl());
        basicDataSource.setUsername(container.getUsername());
        basicDataSource.setPassword(container.getPassword());
        basicDataSource.setDriverClassName(container.getDriverClassName());
        return basicDataSource;
    }

    public DataSource getTestDatabase() {
        if (dataSource == null) {
            throw new AssertionError("not initialized");
        }
        return dataSource;
    }

    public int getPort() {
        if (container == null) {
            throw new AssertionError("not initialized");
        }
        return container.getFirstMappedPort();
    }

    public PostgresDbExtension withAdditionalStartupParameter(String key, String value) {
        additionalParameters.add(Pair.of(key, value));
        return this;
    }
}
