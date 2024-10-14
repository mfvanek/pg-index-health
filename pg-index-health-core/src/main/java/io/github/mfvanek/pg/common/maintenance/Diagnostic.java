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

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * A list of all supported diagnostics with corresponding sql queries and query executors.
 *
 * @author Ivan Vakhrushev
 * @see QueryExecutor
 * @see QueryExecutors
 */
public enum Diagnostic {

    BLOATED_INDEXES(ExecutionTopology.ON_PRIMARY, "bloated_indexes.sql", QueryExecutors::executeQueryWithBloatThreshold, EnumSet.of(CheckType.RUNTIME)),
    BLOATED_TABLES(ExecutionTopology.ON_PRIMARY, "bloated_tables.sql", QueryExecutors::executeQueryWithBloatThreshold, EnumSet.of(CheckType.RUNTIME)),
    DUPLICATED_INDEXES(ExecutionTopology.ON_PRIMARY, "duplicated_indexes.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    FOREIGN_KEYS_WITHOUT_INDEX(ExecutionTopology.ON_PRIMARY, "foreign_keys_without_index.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    INDEXES_WITH_NULL_VALUES(ExecutionTopology.ON_PRIMARY, "indexes_with_null_values.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    INTERSECTED_INDEXES(ExecutionTopology.ON_PRIMARY, "intersected_indexes.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    INVALID_INDEXES(ExecutionTopology.ON_PRIMARY, "invalid_indexes.sql", QueryExecutors::executeQueryWithSchema, EnumSet.allOf(CheckType.class)),
    TABLES_WITH_MISSING_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "tables_with_missing_indexes.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.RUNTIME)),
    TABLES_WITHOUT_PRIMARY_KEY(ExecutionTopology.ON_PRIMARY, "tables_without_primary_key.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    UNUSED_INDEXES(ExecutionTopology.ACROSS_CLUSTER, "unused_indexes.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.RUNTIME)),
    TABLES_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "tables_without_description.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    COLUMNS_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "columns_without_description.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    COLUMNS_WITH_JSON_TYPE(ExecutionTopology.ON_PRIMARY, "columns_with_json_type.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    COLUMNS_WITH_SERIAL_TYPES(ExecutionTopology.ON_PRIMARY, "non_primary_key_columns_with_serial_types.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    FUNCTIONS_WITHOUT_DESCRIPTION(ExecutionTopology.ON_PRIMARY, "functions_without_description.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    INDEXES_WITH_BOOLEAN(ExecutionTopology.ON_PRIMARY, "indexes_with_boolean.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    NOT_VALID_CONSTRAINTS(ExecutionTopology.ON_PRIMARY, "check_not_valid_constraints.sql", QueryExecutors::executeQueryWithSchema, EnumSet.allOf(CheckType.class)),
    BTREE_INDEXES_ON_ARRAY_COLUMNS(ExecutionTopology.ON_PRIMARY, "btree_indexes_on_array_columns.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    SEQUENCE_OVERFLOW(ExecutionTopology.ON_PRIMARY, "sequence_overflow.sql", QueryExecutors::executeQueryWithRemainingPercentageThreshold, EnumSet.of(CheckType.RUNTIME)),
    PRIMARY_KEYS_WITH_SERIAL_TYPES(ExecutionTopology.ON_PRIMARY, "primary_keys_with_serial_types.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    DUPLICATED_FOREIGN_KEYS(ExecutionTopology.ON_PRIMARY, "duplicated_foreign_keys.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC)),
    INTERSECTED_FOREIGN_KEYS(ExecutionTopology.ON_PRIMARY, "intersected_foreign_keys.sql", QueryExecutors::executeQueryWithSchema, EnumSet.of(CheckType.STATIC));

    private final ExecutionTopology executionTopology;
    private final String sqlQueryFileName;
    private final QueryExecutor queryExecutor;
    private final Set<CheckType> checkTypes;

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
               @Nonnull final Set<CheckType> checkTypes) {
        this.executionTopology = Objects.requireNonNull(executionTopology, "executionTopology cannot be null");
        this.sqlQueryFileName = Objects.requireNonNull(sqlQueryFileName, "sqlQueryFileName cannot be null");
        this.queryExecutor = Objects.requireNonNull(queryExecutor, "queryExecutor cannot be null");
        this.checkTypes = EnumSet.copyOf(validateCheckTypes(checkTypes));
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
     * Defines whether this diagnostic is {@link CheckType#STATIC} (can be run in unit/integration tests on an empty database).
     *
     * @return true if this is a static check
     */
    public boolean isStaticCheck() {
        return checkTypes.contains(CheckType.STATIC);
    }

    /**
     * Defines whether this diagnostic is {@link CheckType#RUNTIME} (make sense to perform only on a production database with real data and statistics).
     *
     * @return true if this is a runtime check
     */
    public boolean isRuntimeCheck() {
        return checkTypes.contains(CheckType.RUNTIME);
    }

    @Nonnull
    private static Set<CheckType> validateCheckTypes(@Nonnull final Set<CheckType> checkTypes) {
        Objects.requireNonNull(checkTypes, "checkTypes cannot be null");
        if (checkTypes.isEmpty()) {
            throw new IllegalArgumentException("checkTypes cannot be empty");
        }
        return checkTypes;
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

    /**
     * Defines a type of database check.
     *
     * @author Ivan Vakhrushev
     * @since 0.13.2
     */
    public enum CheckType {
        /**
         * Static check is a check that can be run in unit/integration tests on an empty database.
         * All static checks can be performed at runtime as well.
         */
        STATIC,
        /**
         * Runtime check is a check that make sense to perform only on a production database with real data and statistics.
         * Runtime checks usually require aggregating data from all nodes in the cluster.
         *
         * @see ExecutionTopology#ACROSS_CLUSTER
         */
        RUNTIME
    }
}
