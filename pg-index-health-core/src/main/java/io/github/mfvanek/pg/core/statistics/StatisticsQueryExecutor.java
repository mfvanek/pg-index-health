/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;

import java.util.List;

/**
 * An abstraction of SQL query executor without schema.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
@FunctionalInterface
public interface StatisticsQueryExecutor {

    /**
     * Executes an SQL query on the given PostgreSQL connection and maps the result set
     * into a list of objects using the provided {@code ResultSetExtractor}.
     *
     * @param <T>          the type of objects in the returned list
     * @param pgConnection the PostgreSQL connection to be used for executing the query
     * @param sqlQuery     the SQL query to be executed
     * @param rse          the {@code ResultSetExtractor} used to map rows from the result set into objects of type {@code T}
     * @return a list of objects of type {@code T} created by mapping rows from the result set
     */
    <T> List<T> executeQuery(PgConnection pgConnection,
                             String sqlQuery,
                             ResultSetExtractor<T> rse);
}
