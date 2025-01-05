/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.sql.ResultSet;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * An abstraction of sql query executor.
 *
 * @author Ivan Vahrushev
 * @see DbObject
 * @since 0.6.0
 */
@FunctionalInterface
public interface QueryExecutor {

    /**
     * Executes the specified SQL query using the provided {@link PgConnection} and {@link PgContext},
     * and extracts the results using the given {@link ResultSetExtractor}.
     *
     * @param pgConnection the connection to the PostgreSQL database, must not be null
     * @param pgContext the context for the PostgreSQL database, must not be null
     * @param sqlQuery the SQL query to be executed, must not be null
     * @param rse the extractor used to extract results from the {@link ResultSet}, must not be null
     * @param <T> the type of {@link DbObject} that the query returns
     * @return a list of results extracted by the {@link ResultSetExtractor}, never null
     */
    @Nonnull
    <T extends DbObject> List<T> executeQuery(@Nonnull PgConnection pgConnection,
                                              @Nonnull PgContext pgContext,
                                              @Nonnull String sqlQuery,
                                              @Nonnull ResultSetExtractor<T> rse);
}
