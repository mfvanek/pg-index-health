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

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.settings.ImportantParam;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = new PostgreSqlContainerWrapper(
            Collections.singletonList(Pair.of(ImportantParam.LOCK_TIMEOUT.getName(), "1000")));

    @Nonnull
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.ofPrimary(getDataSource());
    }

    @Nonnull
    protected static HighAvailabilityPgConnection getHaPgConnection() {
        return HighAvailabilityPgConnectionImpl.of(getPgConnection());
    }

    @Nonnull
    protected static ConnectionCredentials getConnectionCredentials() {
        return ConnectionCredentials.ofUrl(POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Nonnull
    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    protected static int getPort() {
        return POSTGRES.getPort();
    }

    protected void executeTestOnDatabase(@Nonnull final String schemaName,
                                         @Nonnull final DatabaseConfigurer databaseConfigurer,
                                         @Nonnull final Consumer<PgContext> testExecutor) {
        try (DatabasePopulator databasePopulator = DatabasePopulator.builder(getDataSource())) {
            databaseConfigurer.configure(databasePopulator)
                    .withSchema(schemaName)
                    .populate();
            testExecutor.accept(PgContext.of(schemaName, 0));
        }
    }
}
