/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.embedded;

import io.zonky.test.db.postgres.embedded.ConnectionInfo;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import io.zonky.test.db.postgres.embedded.PreparedDbProvider;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * JUnit test extension that provides configurable PostgreSQL instance.
 * Additional layer of abstraction that allow to easily switch between
 * embedded PostgreSQL implementations (otj-opentable, zonkyio, testcontainers).
 *
 * @author Nikolay Kondratyev
 */
public class PostgresDbExtension implements BeforeAllCallback, AfterAllCallback {

    private volatile DataSource dataSource;
    private volatile PreparedDbProvider provider;
    private volatile ConnectionInfo connectionInfo;

    private final List<Consumer<EmbeddedPostgres.Builder>> builderCustomizers = new CopyOnWriteArrayList<>();

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        provider = PreparedDbProvider.forPreparer(dataSource -> {}, builderCustomizers);
        connectionInfo = provider.createNewDatabase();
        dataSource = provider.createDataSourceFromConnectionInfo(connectionInfo);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        dataSource = null;
        connectionInfo = null;
        provider = null;
    }

    public DataSource getTestDatabase() {
        if (dataSource == null) {
            throw new AssertionError("not initialized");
        }
        return dataSource;
    }

    public int getPort() {
        if (connectionInfo == null) {
            throw new AssertionError("not initialized");
        }
        return connectionInfo.getPort();
    }

    public PostgresDbExtension withAdditionalStartupParameter(String key, String value) {
        builderCustomizers.add(builder -> builder.setServerConfig(key, value));
        return this;
    }
}
