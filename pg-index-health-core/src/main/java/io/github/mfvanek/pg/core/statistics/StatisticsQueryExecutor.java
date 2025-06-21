/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
 * An abstraction of sql query executor without schema.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
@FunctionalInterface
public interface StatisticsQueryExecutor {

    <T> List<T> executeQuery(PgConnection pgConnection,
                             String sqlQuery,
                             ResultSetExtractor<T> rse);
}
