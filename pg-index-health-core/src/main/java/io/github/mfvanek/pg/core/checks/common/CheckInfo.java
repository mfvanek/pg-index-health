/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

/**
 * Represents an entity that can identify, process, and execute SQL-based checks or diagnostics.
 * This interface supports retrieving metadata about such checks and defines methods
 * for accessing their associated SQL execution details.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public interface CheckInfo extends CheckTypeAware, TopologyAware {

    /**
     * Retrieves the associated JDBC-compatible SQL query.
     *
     * @return SQL query
     */
    String getSqlQuery();

    /**
     * Retrieves the lambda which executes the associated SQL query.
     *
     * @return {@code QueryExecutor}
     */
    QueryExecutor getQueryExecutor();
}
