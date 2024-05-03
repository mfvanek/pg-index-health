/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about size and columns.
 *
 * @author Ivan Vahrushev
 * @since 0.11.0
 */
@Immutable
public class IndexWithColumns extends IndexWithSize {

    private final List<Column> columns;

    @SuppressWarnings("WeakerAccess")
    protected IndexWithColumns(@Nonnull final String tableName,
                               @Nonnull final String indexName,
                               final long indexSizeInBytes,
                               @Nonnull final List<Column> columns) {
        super(tableName, indexName, indexSizeInBytes);
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, "columns cannot be null"));
        Validators.validateThatTableIsTheSame(tableName, defensiveCopy);
        this.columns = defensiveCopy;
    }

    /**
     * Gets columns in index.
     *
     * @return list of columns
     */
    @Nonnull
    public List<Column> getColumns() {
        return columns;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String innerToString() {
        return super.innerToString() + ", columns=" + columns;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return IndexWithColumns.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * Constructs an {@code IndexWithColumns} object with one column.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param column           column in index.
     * @return {@code IndexWithColumns}
     */
    @Nonnull
    public static IndexWithColumns ofSingle(@Nonnull final String tableName,
                                            @Nonnull final String indexName,
                                            final long indexSizeInBytes,
                                            @Nonnull final Column column) {
        final List<Column> columns = List.of(Objects.requireNonNull(column, "column cannot be null"));
        return new IndexWithColumns(tableName, indexName, indexSizeInBytes, columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given columns.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          columns in index.
     * @return {@code IndexWithColumns}
     */
    @Nonnull
    public static IndexWithColumns ofColumns(@Nonnull final String tableName,
                                             @Nonnull final String indexName,
                                             final long indexSizeInBytes,
                                             @Nonnull final List<Column> columns) {
        return new IndexWithColumns(tableName, indexName, indexSizeInBytes, columns);
    }
}
