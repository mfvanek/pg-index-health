/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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
public enum Diagnostic implements CheckTypeAware {

    BLOATED_INDEXES(ExecutionTopology.ON_PRIMARY, "bloated_indexes.sql", QueryExecutors::executeQueryWithBloatThreshold, true),
    BLOATED_TABLES(ExecutionTopology.ON_PRIMARY, "bloated_tables.sql", QueryExecutors::executeQueryWithBloatThreshold, true),
    DUPLICATED_INDEXES(ExecutionTopology.ON_PRIMARY, "duplicated_indexes.sql", QueryExecutors::executeQueryWithSchema, false),
    FOREIGN_KEYS_WITHOUT_INDEX(ExecutionTopology.ON_PRIMARY, "foreign_keys_without_index.sql", QueryExecutors::executeQueryWithSchema, false),
    INDEXES_WITH_NULL_VALUES(ExecutionTopology.ON_PRIMARY, "indexes_with_null_values.sql", QueryExecutors::executeQueryWithSchema, false),
    INTERSECTED_INDEXES(ExecutionTopology.ON_PRIMARY, "intersected_indexes.sql", QueryExecutors::executeQueryWithSchema, false),
    INVALID_INDEXES(ExecutionTopology.ON_PRIMARY, "invalid_indexes.sql", QueryExecutors::executeQueryWithSchema, false),
    TABLES_WITH_MISSING_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "tables_with_missing_indexes.sql", QueryExecutors::executeQueryWithSchema, true),
    TABLES_WITHOUT_PRIMARY_KEY(ExecutionTopology.ON_PRIMARY, "tables_without_primary_key.sql", QueryExecutors::executeQueryWithSchema, false),
    UNUSED_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "unused_indexes.sql", QueryExecutors::executeQueryWithSchema, true),
    TABLES_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "tables_without_description.sql", QueryExecutors::executeQueryWithSchema, false),
    COLUMNS_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "columns_without_description.sql", QueryExecutors::executeQueryWithSchema, false),
    COLUMNS_WITH_JSON_TYPE(ExecutionTopology.ON_PRIMARY, "columns_with_json_type.sql", QueryExecutors::executeQueryWithSchema, false),
    COLUMNS_WITH_SERIAL_TYPES(ExecutionTopology.ON_PRIMARY, "columns_with_serial_types.sql", QueryExecutors::executeQueryWithSchema, false),
    FUNCTIONS_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "functions_without_description.sql", QueryExecutors::executeQueryWithSchema, false),
    INDEXES_WITH_BOOLEAN(ExecutionTopology.ON_PRIMARY, "indexes_with_boolean.sql", QueryExecutors::executeQueryWithSchema, false),
    NOT_VALID_CONSTRAINTS(ExecutionTopology.ON_PRIMARY, "not_valid_constraints.sql", QueryExecutors::executeQueryWithSchema, false),
    BTREE_INDEXES_ON_ARRAY_COLUMNS(ExecutionTopology.ON_PRIMARY, "btree_indexes_on_array_columns.sql", QueryExecutors::executeQueryWithSchema, false),
    SEQUENCE_OVERFLOW(ExecutionTopology.ON_PRIMARY, "sequence_overflow.sql", QueryExecutors::executeQueryWithRemainingPercentageThreshold, true),
    PRIMARY_KEYS_WITH_SERIAL_TYPES(ExecutionTopology.ON_PRIMARY, "primary_keys_with_serial_types.sql", QueryExecutors::executeQueryWithSchema, false),
    DUPLICATED_FOREIGN_KEYS(ExecutionTopology.ON_PRIMARY, "duplicated_foreign_keys.sql", QueryExecutors::executeQueryWithSchema, false),
    INTERSECTED_FOREIGN_KEYS(ExecutionTopology.ON_PRIMARY, "intersected_foreign_keys.sql", QueryExecutors::executeQueryWithSchema, false),
    POSSIBLE_OBJECT_NAME_OVERFLOW(ExecutionTopology.ON_PRIMARY, "possible_object_name_overflow.sql", QueryExecutors::executeQueryWithSchema, false),
    TABLES_NOT_LINKED_TO_OTHERS(ExecutionTopology.ON_PRIMARY, "tables_not_linked_to_others.sql", QueryExecutors::executeQueryWithSchema, false),
    FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE(ExecutionTopology.ON_PRIMARY, "foreign_keys_with_unmatched_column_type.sql", QueryExecutors::executeQueryWithSchema, false);

    private final ExecutionTopology executionTopology;
    private final String sqlQueryFileName;
    private final QueryExecutor queryExecutor;
    private final boolean runtimeCheck;

    /**
     * Creates a {@code Diagnostic} instance.
     *
     * @param executionTopology the place where the diagnostic should be executed
     * @param sqlQueryFileName  the associated sql query file name
     * @param queryExecutor     the lambda which executes the associated sql query
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
     * Gets the place where the diagnostic should be executed.
     *
     * @return {@code ExecutionTopology}
     */
    @Nonnull
    public ExecutionTopology getExecutionTopology() {
        return executionTopology;
    }

    /**
     * Gets the associated sql query file name.
     *
     * @return sql query file name
     */
    @Nonnull
    public String getSqlQueryFileName() {
        return sqlQueryFileName;
    }

    /**
     * Gets the lambda which executes the associated sql query.
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
