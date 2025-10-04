/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.factory.ConnectionCredentials;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.testing.PostgreSqlContainerWrapper;

import java.util.function.Consumer;
import javax.sql.DataSource;

public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = PostgreSqlContainerWrapper.withDefaultVersion();

    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.of(getDataSource(), getHost());
    }

    protected static PgHost getHost() {
        return PgHostImpl.ofUrl(POSTGRES.getUrl());
    }

    protected static HighAvailabilityPgConnection getHaPgConnection() {
        return HighAvailabilityPgConnectionImpl.of(getPgConnection());
    }

    protected static ConnectionCredentials getConnectionCredentials() {
        return ConnectionCredentials.ofUrl(POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    protected void executeTestOnDatabase(final String schemaName,
                                         final DatabaseConfigurer databaseConfigurer,
                                         final Consumer<PgContext> testExecutor) {
        try (DatabasePopulator databasePopulator = DatabasePopulator.builder(getDataSource(), schemaName, isProceduresSupported())) {
            databaseConfigurer.configure(databasePopulator)
                .populate();
            testExecutor.accept(PgContext.of(schemaName, 0));
        }
    }

    protected static boolean isProceduresSupported() {
        return POSTGRES.isProceduresSupported();
    }

    protected static boolean isProceduresNotSupported() {
        return !isProceduresSupported();
    }

    protected static boolean isOutParametersInProcedureSupported() {
        return POSTGRES.isOutParametersInProcedureSupported();
    }

    protected static boolean isNotNullConstraintsSupported() {
        return POSTGRES.isNotNullConstraintsSupported();
    }
}
