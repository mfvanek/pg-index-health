/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import io.github.mfvanek.pg.core.utils.QueryExecutors;

import java.util.Objects;
import java.util.function.Function;

/**
 * A list of standard diagnostics with corresponding SQL queries and query executors.
 *
 * @author Ivan Vakhrushev
 * @see QueryExecutor
 * @see QueryExecutors
 */
public enum Diagnostic implements CheckInfo {

    /**
     * Check for indexes bloat.
     */
    BLOATED_INDEXES(StandardCheckInfo::ofBloat),
    /**
     * Check for tables bloat.
     */
    BLOATED_TABLES(StandardCheckInfo::ofBloat),
    /**
     * Check for duplicated (completely identical) indexes.
     */
    DUPLICATED_INDEXES,
    /**
     * Check for foreign keys without associated indexes.
     */
    FOREIGN_KEYS_WITHOUT_INDEX,
    /**
     * Check for indexes with null values.
     */
    INDEXES_WITH_NULL_VALUES,
    /**
     * Check for intersected (partially identical) indexes.
     */
    INTERSECTED_INDEXES,
    /**
     * Check for invalid (broken) indexes.
     */
    INVALID_INDEXES,
    /**
     * Check for tables with potentially missing indexes.
     */
    TABLES_WITH_MISSING_INDEXES(StandardCheckInfo::ofCluster),
    /**
     * Check for tables without a primary key.
     */
    TABLES_WITHOUT_PRIMARY_KEY,
    /**
     * Check for unused indexes.
     */
    UNUSED_INDEXES(StandardCheckInfo::ofCluster),
    /**
     * Check for tables without description.
     */
    TABLES_WITHOUT_DESCRIPTION,
    /**
     * Check for columns without description.
     */
    COLUMNS_WITHOUT_DESCRIPTION,
    /**
     * Check for columns with {@code JSON} type.
     */
    COLUMNS_WITH_JSON_TYPE,
    /**
     * Check for columns of serial types that are not primary keys.
     */
    COLUMNS_WITH_SERIAL_TYPES,
    /**
     * Check for procedures/functions without description.
     */
    FUNCTIONS_WITHOUT_DESCRIPTION,
    /**
     * Check for indexes that contain boolean values.
     */
    INDEXES_WITH_BOOLEAN,
    /**
     * Check for not valid constraints.
     */
    NOT_VALID_CONSTRAINTS,
    /**
     * Check for B-tree indexes on array columns.
     */
    BTREE_INDEXES_ON_ARRAY_COLUMNS,
    /**
     * Check for sequence overflow.
     */
    SEQUENCE_OVERFLOW(StandardCheckInfo::ofRemainingPercentage),
    /**
     * Check for primary keys columns with serial types (smallserial/serial/bigserial).
     */
    PRIMARY_KEYS_WITH_SERIAL_TYPES,
    /**
     * Check for duplicated (completely identical) foreign keys.
     */
    DUPLICATED_FOREIGN_KEYS,
    /**
     * Check for intersected (partially identical) foreign keys.
     */
    INTERSECTED_FOREIGN_KEYS,
    /**
     * Check for objects whose names have a length of {@code max_identifier_length}.
     */
    POSSIBLE_OBJECT_NAME_OVERFLOW,
    /**
     * Check for tables that are not linked to other tables.
     */
    TABLES_NOT_LINKED_TO_OTHERS,
    /**
     * Check for foreign keys where the type of the constrained column does not match the type in the referenced table.
     */
    FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE,
    /**
     * Check for tables with zero or one columns.
     */
    TABLES_WITH_ZERO_OR_ONE_COLUMN,
    /**
     * Check for objects whose names do not follow the naming convention.
     */
    OBJECTS_NOT_FOLLOWING_NAMING_CONVENTION,
    /**
     * Check for columns whose names do not follow the naming convention.
     */
    COLUMNS_NOT_FOLLOWING_NAMING_CONVENTION,
    /**
     * Check for primary keys with columns of a fixed length varchar type.
     */
    PRIMARY_KEYS_WITH_VARCHAR,
    /**
     * Check for columns with a fixed length varchar type.
     */
    COLUMNS_WITH_FIXED_LENGTH_VARCHAR,
    /**
     * Check for indexes with unnecessary where-clause on a not null column.
     */
    INDEXES_WITH_UNNECESSARY_WHERE_CLAUSE,
    /**
     * Check for primary keys that are the most likely natural keys.
     */
    PRIMARY_KEYS_THAT_MOST_LIKELY_NATURAL_KEYS,
    /**
     * Check for columns with {@code money} type.
     */
    COLUMNS_WITH_MONEY_TYPE,
    /**
     * Check for indexes in which columns with the timestamp\timestamptz type are not the last.
     */
    INDEXES_WITH_TIMESTAMP_IN_THE_MIDDLE,
    /**
     * Check for columns with {@code timestamp} or {@code timetz} type.
     */
    COLUMNS_WITH_TIMESTAMP_OR_TIMETZ_TYPE,
    /**
     * Check for tables where the primary key column is not the first column in the table.
     */
    TABLES_WHERE_PRIMARY_KEY_COLUMNS_NOT_FIRST,
    /**
     * Check for tables that have all columns besides the primary key that are nullable.
     */
    TABLES_WHERE_ALL_COLUMNS_NULLABLE_EXCEPT_PK;

    private final CheckInfo inner;

    /**
     * Constructs a new instance of the Diagnostic class.
     * This constructor initializes the inner field with a static CheckInfo instance based on the name of the current object.
     */
    Diagnostic() {
        this.inner = StandardCheckInfo.ofStatic(name());
    }

    /**
     * Constructs a new instance of the Diagnostic class with a factory for creating {@code CheckInfo}.
     * The constructor initializes the inner field using a {@code CheckInfo} instance
     * generated by applying the provided factory to the name of the current object.
     *
     * @param checkInfoFactory a factory function that accepts a {@code String} (the name of the current object)
     *                         and produces a {@code CheckInfo} instance; must not be null
     */
    Diagnostic(final Function<String, CheckInfo> checkInfoFactory) {
        this.inner = Objects.requireNonNull(checkInfoFactory).apply(name());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return inner.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExecutionTopology getExecutionTopology() {
        return inner.getExecutionTopology();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSqlQuery() {
        return inner.getSqlQuery();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryExecutor getQueryExecutor() {
        return inner.getQueryExecutor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRuntime() {
        return inner.isRuntime();
    }
}
