/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.utils.QueryExecutors;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A list of all supported diagnostics with corresponding sql queries and query executors.
 *
 * @author Ivan Vakhrushev
 * @see QueryExecutor
 * @see QueryExecutors
 */
public enum Diagnostic {

    BLOATED_INDEXES("bloated_indexes.sql", QueryExecutors::executeQueryWithBloatThreshold),
    BLOATED_TABLES("bloated_tables.sql", QueryExecutors::executeQueryWithBloatThreshold),
    DUPLICATED_INDEXES("duplicated_indexes.sql", QueryExecutors::executeQueryWithSchema),
    FOREIGN_KEYS_WITHOUT_INDEX("foreign_keys_without_index.sql", QueryExecutors::executeQueryWithSchema),
    INDEXES_WITH_NULL_VALUES("indexes_with_null_values.sql", QueryExecutors::executeQueryWithSchema),
    INTERSECTED_INDEXES("intersected_indexes.sql", QueryExecutors::executeQueryWithSchema),
    INVALID_INDEXES("invalid_indexes.sql", QueryExecutors::executeQueryWithSchema),
    TABLES_WITH_MISSING_INDEXES("tables_with_missing_indexes.sql", QueryExecutors::executeQueryWithSchema),
    TABLES_WITHOUT_PRIMARY_KEY("tables_without_primary_key.sql", QueryExecutors::executeQueryWithSchema),
    UNUSED_INDEXES("unused_indexes.sql", QueryExecutors::executeQueryWithSchema);

    private final String sqlQueryFileName;
    private final QueryExecutor queryExecutor;

    Diagnostic(@Nonnull final String sqlQueryFileName, @Nonnull final QueryExecutor queryExecutor) {
        this.sqlQueryFileName = Objects.requireNonNull(sqlQueryFileName, "sqlQueryFileName cannot be null");
        this.queryExecutor = Objects.requireNonNull(queryExecutor, "queryExecutor cannot be null");
    }

    @Nonnull
    public String getSqlQueryFileName() {
        return sqlQueryFileName;
    }

    @Nonnull
    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }
}
