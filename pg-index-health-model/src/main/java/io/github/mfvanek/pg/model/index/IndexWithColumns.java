/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about size and columns.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
@Immutable
public class IndexWithColumns extends IndexWithSize implements ColumnsAware {

    private final List<Column> columns;

    /**
     * Constructs an {@code IndexWithColumns} with the specified table name, index name, size in bytes, and list of columns.
     *
     * @param tableName        the name of the table associated with this index; must be non-blank.
     * @param indexName        the name of the index; must be non-blank.
     * @param indexSizeInBytes the size of the index in bytes; must be zero or positive.
     * @param columns          the list of columns associated with the index; cannot be null.
     */
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
     * Retrieves columns in index (one or more).
     *
     * @return list of columns
     */
    @Nonnull
    @Override
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
     * Constructs an {@code IndexWithColumns} object with one column and given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param column           column in index.
     * @return {@code IndexWithColumns}
     * @since 0.14.3
     */
    @Nonnull
    public static IndexWithColumns ofSingle(@Nonnull final PgContext pgContext,
                                            @Nonnull final String tableName,
                                            @Nonnull final String indexName,
                                            final long indexSizeInBytes,
                                            @Nonnull final Column column) {
        return ofSingle(PgContext.enrichWith(tableName, pgContext), PgContext.enrichWith(indexName, pgContext), indexSizeInBytes, column);
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

    /**
     * Constructs an {@code IndexWithColumns} object with given columns and context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          columns in index.
     * @return {@code IndexWithColumns}
     * @since 0.14.3
     */
    @Nonnull
    public static IndexWithColumns ofColumns(@Nonnull final PgContext pgContext,
                                             @Nonnull final String tableName,
                                             @Nonnull final String indexName,
                                             final long indexSizeInBytes,
                                             @Nonnull final List<Column> columns) {
        return ofColumns(PgContext.enrichWith(tableName, pgContext), PgContext.enrichWith(indexName, pgContext), indexSizeInBytes, columns);
    }
}
