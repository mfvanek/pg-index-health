/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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

/**
 * Base class for database-aware tests running against an embedded or containerized PostgreSQL instance.
 */
public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = PostgreSqlContainerWrapper.withDefaultVersion();

    /**
     * Constructs a new instance of {@code DatabaseAwareTestBase}.
     */
    protected DatabaseAwareTestBase() {
    }

    /**
     * Gets a connection to the PostgreSQL database.
     *
     * @return a {@link PgConnection} instance
     */
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.of(getDataSource(), getHost());
    }

    /**
     * Gets host information for the PostgreSQL database.
     *
     * @return a {@link PgHost} representing the database host
     */
    protected static PgHost getHost() {
        return PgHostImpl.ofUrl(POSTGRES.getUrl());
    }

    /**
     * Gets a high-availability connection wrapper for the PostgreSQL database.
     *
     * @return a {@link HighAvailabilityPgConnection} instance
     */
    protected static HighAvailabilityPgConnection getHaPgConnection() {
        return HighAvailabilityPgConnectionImpl.of(getPgConnection());
    }

    /**
     * Gets the connection credentials for the PostgreSQL database.
     *
     * @return a {@link ConnectionCredentials} instance
     */
    protected static ConnectionCredentials getConnectionCredentials() {
        return ConnectionCredentials.ofUrl(POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    /**
     * Gets the configured data source for the PostgreSQL database.
     *
     * @return the {@link DataSource}
     */
    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    /**
     * Prepares the database schema and runs the specified test callback.
     *
     * @param schemaName         the schema name to use
     * @param databaseConfigurer a configurer to populate the database
     * @param testExecutor       the test logic to execute
     */
    protected void executeTestOnDatabase(final String schemaName,
                                         final DatabaseConfigurer databaseConfigurer,
                                         final Consumer<PgContext> testExecutor) {
        try (DatabasePopulator databasePopulator = DatabasePopulator.builder(getDataSource(), schemaName, isProceduresSupported(), isUnloggedSequencesSupported())) {
            databaseConfigurer.configure(databasePopulator)
                .populate();
            testExecutor.accept(PgContext.of(schemaName, 0));
        }
    }

    /**
     * Checks if stored procedures are supported by the PostgreSQL server version.
     *
     * @return {@code true} if stored procedures are supported, {@code false} otherwise
     */
    protected static boolean isProceduresSupported() {
        return POSTGRES.isProceduresSupported();
    }

    /**
     * Checks if stored procedures are not supported by the PostgreSQL server version.
     *
     * @return {@code true} if stored procedures are not supported, {@code false} otherwise
     */
    protected static boolean isProceduresNotSupported() {
        return !isProceduresSupported();
    }

    /**
     * Checks if OUT parameters in procedures are supported by the PostgreSQL server version.
     *
     * @return {@code true} if OUT parameters are supported, {@code false} otherwise
     */
    protected static boolean isOutParametersInProcedureSupported() {
        return POSTGRES.isOutParametersInProcedureSupported();
    }

    /**
     * Checks if NOT NULL constraints syntax is supported by the PostgreSQL server version.
     *
     * @return {@code true} if NOT NULL constraints syntax is supported, {@code false} otherwise
     */
    protected static boolean isNotNullConstraintsSupported() {
        return POSTGRES.isNotNullConstraintsSupported();
    }

    /**
     * Checks if unlogged sequences are supported by the PostgreSQL server version.
     *
     * @return {@code true} if unlogged sequences are supported, {@code false} otherwise
     */
    protected static boolean isUnloggedSequencesSupported() {
        return POSTGRES.isUnloggedSequencesSupported();
    }
}
