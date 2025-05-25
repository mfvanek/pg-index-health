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
public final class IndexWithColumns extends AbstractIndexAware implements ColumnsAware, Comparable<IndexWithColumns> {

    private final List<Column> columns;

    private IndexWithColumns(@Nonnull final Index index,
                             @Nonnull final List<Column> columns) {
        super(index);
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, "columns cannot be null"));
        Validators.validateThatTableIsTheSame(index.getTableName(), defensiveCopy);
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
    public String toString() {
        return IndexWithColumns.class.getSimpleName() + '{' +
            index.innerToString() +
            ", columns=" + columns +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof IndexWithColumns)) {
            return false;
        }

        final IndexWithColumns that = (IndexWithColumns) other;
        return Objects.equals(index, that.index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final IndexWithColumns other) {
        Objects.requireNonNull(other, "other cannot be null");
        return index.compareTo(other.index);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with one column.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param column           column in index; must be non-null.
     * @return {@code IndexWithColumns}
     */
    @Nonnull
    public static IndexWithColumns ofSingle(@Nonnull final String tableName,
                                            @Nonnull final String indexName,
                                            final long indexSizeInBytes,
                                            @Nonnull final Column column) {
        return ofSingle(Index.of(tableName, indexName, indexSizeInBytes), column);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with one column and given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param column           column in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.14.3
     */
    @Nonnull
    public static IndexWithColumns ofSingle(@Nonnull final PgContext pgContext,
                                            @Nonnull final String tableName,
                                            @Nonnull final String indexName,
                                            final long indexSizeInBytes,
                                            @Nonnull final Column column) {
        return ofSingle(Index.of(pgContext, tableName, indexName, indexSizeInBytes), column);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with one column and given index.
     *
     * @param index  index; must be non-null.
     * @param column column in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    @Nonnull
    public static IndexWithColumns ofSingle(@Nonnull final Index index,
                                            @Nonnull final Column column) {
        final List<Column> columns = List.of(Objects.requireNonNull(column, "column cannot be null"));
        return of(index, columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column and given context.
     *
     * @param pgContext  the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    @Nonnull
    public static IndexWithColumns ofNullable(@Nonnull final PgContext pgContext,
                                              @Nonnull final String tableName,
                                              @Nonnull final String indexName,
                                              @Nonnull final String columnName) {
        return ofSingle(Index.of(pgContext, tableName, indexName), Column.ofNullable(pgContext, tableName, columnName));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column and given context.
     *
     * @param pgContext  the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    @Nonnull
    public static IndexWithColumns ofNotNull(@Nonnull final PgContext pgContext,
                                             @Nonnull final String tableName,
                                             @Nonnull final String indexName,
                                             @Nonnull final String columnName) {
        return ofSingle(Index.of(pgContext, tableName, indexName), Column.ofNotNull(pgContext, tableName, columnName));
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given columns.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     */
    @Nonnull
    public static IndexWithColumns ofColumns(@Nonnull final String tableName,
                                             @Nonnull final String indexName,
                                             final long indexSizeInBytes,
                                             @Nonnull final List<Column> columns) {
        return of(Index.of(tableName, indexName, indexSizeInBytes), columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given columns and context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.14.3
     */
    @Nonnull
    public static IndexWithColumns ofColumns(@Nonnull final PgContext pgContext,
                                             @Nonnull final String tableName,
                                             @Nonnull final String indexName,
                                             final long indexSizeInBytes,
                                             @Nonnull final List<Column> columns) {
        return of(Index.of(pgContext, tableName, indexName, indexSizeInBytes), columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given index and columns.
     *
     * @param index   index; must be non-null.
     * @param columns columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    @Nonnull
    public static IndexWithColumns of(@Nonnull final Index index,
                                      @Nonnull final List<Column> columns) {
        return new IndexWithColumns(index, columns);
    }
}
