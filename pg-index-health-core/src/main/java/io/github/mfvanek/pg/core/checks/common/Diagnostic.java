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

import io.github.mfvanek.pg.core.utils.QueryExecutors;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A list of all supported diagnostics with corresponding sql queries and query executors.
 *
 * @author Ivan Vakhrushev
 * @see QueryExecutor
 * @see QueryExecutors
 */
public enum Diagnostic implements CheckTypeAware {

    BLOATED_INDEXES("bloated_indexes.sql", QueryExecutors::executeQueryWithBloatThreshold, true),
    BLOATED_TABLES("bloated_tables.sql", QueryExecutors::executeQueryWithBloatThreshold, true),
    DUPLICATED_INDEXES("duplicated_indexes.sql"),
    FOREIGN_KEYS_WITHOUT_INDEX("foreign_keys_without_index.sql"),
    INDEXES_WITH_NULL_VALUES("indexes_with_null_values.sql"),
    INTERSECTED_INDEXES("intersected_indexes.sql"),
    INVALID_INDEXES("invalid_indexes.sql"),
    TABLES_WITH_MISSING_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "tables_with_missing_indexes.sql"),
    TABLES_WITHOUT_PRIMARY_KEY("tables_without_primary_key.sql"),
    UNUSED_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "unused_indexes.sql"),
    TABLES_WITHOUT_DESCRIPTION("tables_without_description.sql"),
    COLUMNS_WITHOUT_DESCRIPTION("columns_without_description.sql"),
    COLUMNS_WITH_JSON_TYPE("columns_with_json_type.sql"),
    COLUMNS_WITH_SERIAL_TYPES("columns_with_serial_types.sql"),
    FUNCTIONS_WITHOUT_DESCRIPTION("functions_without_description.sql"),
    INDEXES_WITH_BOOLEAN("indexes_with_boolean.sql"),
    NOT_VALID_CONSTRAINTS("not_valid_constraints.sql"),
    BTREE_INDEXES_ON_ARRAY_COLUMNS("btree_indexes_on_array_columns.sql"),
    SEQUENCE_OVERFLOW("sequence_overflow.sql", QueryExecutors::executeQueryWithRemainingPercentageThreshold, true),
    PRIMARY_KEYS_WITH_SERIAL_TYPES("primary_keys_with_serial_types.sql"),
    DUPLICATED_FOREIGN_KEYS("duplicated_foreign_keys.sql"),
    INTERSECTED_FOREIGN_KEYS("intersected_foreign_keys.sql"),
    POSSIBLE_OBJECT_NAME_OVERFLOW("possible_object_name_overflow.sql"),
    TABLES_NOT_LINKED_TO_OTHERS("tables_not_linked_to_others.sql"),
    FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE("foreign_keys_with_unmatched_column_type.sql"),
    TABLES_WITH_ZERO_OR_ONE_COLUMN("tables_with_zero_or_one_column.sql"),
    OBJECTS_NOT_FOLLOWING_NAMING_CONVENTION("objects_not_following_naming_convention.sql");

    private final ExecutionTopology executionTopology;
    private final String sqlQueryFileName;
    private final QueryExecutor queryExecutor;
    private final boolean runtimeCheck;

    /**
     * Creates a {@code Diagnostic} instance.
     *
     * @param executionTopology the place where the diagnostic should be executed
     * @param sqlQueryFileName  the associated sql query file name; must be non-null
     * @param queryExecutor     the lambda which executes the associated sql query
     * @param runtimeCheck      whether this is a runtime diagnostic or static
     */
    Diagnostic(@Nonnull final ExecutionTopology executionTopology,
               @Nonnull final String sqlQueryFileName,
               @Nonnull final QueryExecutor queryExecutor,
               final boolean runtimeCheck) {
        this.executionTopology = Objects.requireNonNull(executionTopology, "executionTopology cannot be null");
        this.sqlQueryFileName = Objects.requireNonNull(sqlQueryFileName, "sqlQueryFileName cannot be null");
        this.queryExecutor = Objects.requireNonNull(queryExecutor, "queryExecutor cannot be null");
        this.runtimeCheck = runtimeCheck;
    }

    /**
     * Creates a {@code Diagnostic} instance.
     *
     * @param sqlQueryFileName the associated sql query file name; must be non-null
     * @param queryExecutor    the lambda which executes the associated sql query
     * @param runtimeCheck     whether this is a runtime diagnostic or static
     */
    Diagnostic(@Nonnull final String sqlQueryFileName,
               @Nonnull final QueryExecutor queryExecutor,
               final boolean runtimeCheck) {
        this(ExecutionTopology.ON_PRIMARY, sqlQueryFileName, queryExecutor, runtimeCheck);
    }

    /**
     * Creates a schema-aware runtime {@code Diagnostic} instance.
     *
     * @param executionTopology the place where the diagnostic should be executed
     * @param sqlQueryFileName  the associated sql query file name; must be non-null
     */
    Diagnostic(@Nonnull final ExecutionTopology executionTopology,
               @Nonnull final String sqlQueryFileName) {
        this(executionTopology, sqlQueryFileName, QueryExecutors::executeQueryWithSchema, true);
    }

    /**
     * Creates a schema-aware static {@code Diagnostic} instance with ExecutionTopology.ON_PRIMARY.
     *
     * @param sqlQueryFileName the associated sql query file name; must be non-null
     */
    Diagnostic(@Nonnull final String sqlQueryFileName) {
        this(ExecutionTopology.ON_PRIMARY, sqlQueryFileName, QueryExecutors::executeQueryWithSchema, false);
    }

    /**
     * Retrieves the place where the diagnostic should be executed.
     *
     * @return {@code ExecutionTopology}
     */
    @Nonnull
    public ExecutionTopology getExecutionTopology() {
        return executionTopology;
    }

    /**
     * Retrieves the associated sql query file name.
     *
     * @return sql query file name
     */
    @Nonnull
    public String getSqlQueryFileName() {
        return sqlQueryFileName;
    }

    /**
     * Retrieves the lambda which executes the associated sql query.
     *
     * @return {@code QueryExecutor}
     */
    @Nonnull
    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    /**
     * Shows whether diagnostic results should be collected from all nodes in the cluster.
     *
     * @return true if diagnostic results should be collected from all nodes in the cluster
     */
    public boolean isAcrossCluster() {
        return executionTopology == ExecutionTopology.ACROSS_CLUSTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRuntime() {
        return runtimeCheck;
    }

    /**
     * Defines the place where the diagnostic should be executed.
     *
     * @author Ivan Vakhrushev
     * @since 0.6.0
     */
    public enum ExecutionTopology {
        /**
         * Only on primary host.
         */
        ON_PRIMARY,
        /**
         * Across the entire database cluster.
         */
        ACROSS_CLUSTER
    }
}
