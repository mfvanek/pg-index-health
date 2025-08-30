/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;

/**
 * An immutable representation of a database index with information about size and columns.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
@SuppressWarnings("checkstyle:EqualsHashCode")
public final class IndexWithColumns extends AbstractIndexAware implements ColumnsAware, Comparable<IndexWithColumns> {

    private final List<Column> columns;

    private IndexWithColumns(final Index index,
                             final List<Column> columns) {
        super(index);
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, COLUMNS_FIELD + " cannot be null"));
        Validators.validateThatTableIsTheSame(index.getTableName(), defensiveCopy);
        this.columns = defensiveCopy;
    }

    /**
     * Retrieves columns in index (one or more).
     *
     * @return list of columns
     */
    @Override
    public List<ColumnNameAware> getColumns() {
        return List.copyOf(columns);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return IndexWithColumns.class.getSimpleName() + '{' +
            index.innerToString() +
            ", " + COLUMNS_FIELD + '=' + columns +
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

        if (!(other instanceof final IndexWithColumns that)) {
            return false;
        }

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
    public int compareTo(final IndexWithColumns other) {
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
    public static IndexWithColumns ofSingle(final String tableName,
                                            final String indexName,
                                            final long indexSizeInBytes,
                                            final Column column) {
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
    public static IndexWithColumns ofSingle(final PgContext pgContext,
                                            final String tableName,
                                            final String indexName,
                                            final long indexSizeInBytes,
                                            final Column column) {
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
    public static IndexWithColumns ofSingle(final Index index,
                                            final Column column) {
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
    public static IndexWithColumns ofNullable(final PgContext pgContext,
                                              final String tableName,
                                              final String indexName,
                                              final String columnName) {
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
    public static IndexWithColumns ofNotNull(final PgContext pgContext,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName) {
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
    public static IndexWithColumns ofColumns(final String tableName,
                                             final String indexName,
                                             final long indexSizeInBytes,
                                             final List<Column> columns) {
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
    public static IndexWithColumns ofColumns(final PgContext pgContext,
                                             final String tableName,
                                             final String indexName,
                                             final long indexSizeInBytes,
                                             final List<Column> columns) {
        return of(Index.of(pgContext, tableName, indexName, indexSizeInBytes), columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with zero size, given columns and context.
     *
     * @param pgContext the schema context to enrich table and index name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @param columns   columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.20.2
     */
    public static IndexWithColumns ofColumns(final PgContext pgContext,
                                             final String tableName,
                                             final String indexName,
                                             final List<Column> columns) {
        return ofColumns(pgContext, tableName, indexName, 0L, columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given index and columns.
     *
     * @param index   index; must be non-null.
     * @param columns columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    public static IndexWithColumns of(final Index index,
                                      final List<Column> columns) {
        return new IndexWithColumns(index, columns);
    }
}
