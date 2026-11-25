/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;

/**
 * An immutable representation of a database index with information about size and columns.
 * <p>
 * The order of columns is significant: it is the column order in the index definition,
 * so indexes on {@code (a, b)} and {@code (b, a)} are different.
 * That is why the factories accept a {@link List} or varargs rather than an arbitrary {@link java.util.Collection}.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
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
     * Constructs an {@code IndexWithColumns} object with given columns and context.
     *
     * @param ctx              the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param column           the first column in index; must be non-null.
     * @param otherColumns     other columns in index; must not contain null elements.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns of(final PgContext ctx,
                                      final String tableName,
                                      final String indexName,
                                      final long indexSizeInBytes,
                                      final Column column,
                                      final Column... otherColumns) {
        return of(Index.of(ctx, tableName, indexName, indexSizeInBytes), column, otherColumns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with zero size, given columns and context.
     *
     * @param ctx          the schema context to enrich table and index name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param indexName    index name; should be non-blank.
     * @param column       the first column in index; must be non-null.
     * @param otherColumns other columns in index; must not contain null elements.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns of(final PgContext ctx,
                                      final String tableName,
                                      final String indexName,
                                      final Column column,
                                      final Column... otherColumns) {
        return of(Index.of(ctx, tableName, indexName), column, otherColumns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given index and columns.
     *
     * @param index        index; must be non-null.
     * @param column       the first column in index; must be non-null.
     * @param otherColumns other columns in index; must not contain null elements.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns of(final Index index,
                                      final Column column,
                                      final Column... otherColumns) {
        return of(index, Validators.columnsNotNull(column, otherColumns));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullable(final PgContext ctx,
                                              final String tableName,
                                              final String indexName,
                                              final String columnName,
                                              final String columnType) {
        return of(Index.of(ctx, tableName, indexName), Column.ofNullable(ctx, tableName, columnName, columnType));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullable(final PgContext ctx,
                                              final String tableName,
                                              final String indexName,
                                              final String columnName,
                                              final PgColumnType columnType) {
        return ofNullable(ctx, tableName, indexName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNotNull(final PgContext ctx,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName,
                                             final String columnType) {
        return of(Index.of(ctx, tableName, indexName), Column.ofNotNull(ctx, tableName, columnName, columnType));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNotNull(final PgContext ctx,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName,
                                             final PgColumnType columnType) {
        return ofNotNull(ctx, tableName, indexName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code bigint} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofBigint(final PgContext ctx,
                                            final String tableName,
                                            final String indexName,
                                            final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code bigint} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableBigint(final PgContext ctx,
                                                    final String tableName,
                                                    final String indexName,
                                                    final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code integer} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofInteger(final PgContext ctx,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code integer} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableInteger(final PgContext ctx,
                                                     final String tableName,
                                                     final String indexName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code character varying} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofVarchar(final PgContext ctx,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code character varying} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableVarchar(final PgContext ctx,
                                                     final String tableName,
                                                     final String indexName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code text} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofText(final PgContext ctx,
                                          final String tableName,
                                          final String indexName,
                                          final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code text} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableText(final PgContext ctx,
                                                  final String tableName,
                                                  final String indexName,
                                                  final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code character} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofCharacter(final PgContext ctx,
                                               final String tableName,
                                               final String indexName,
                                               final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code character} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableCharacter(final PgContext ctx,
                                                       final String tableName,
                                                       final String indexName,
                                                       final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code boolean} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofBoolean(final PgContext ctx,
                                             final String tableName,
                                             final String indexName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code boolean} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableBoolean(final PgContext ctx,
                                                     final String tableName,
                                                     final String indexName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code timestamp without time zone} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofTimestamp(final PgContext ctx,
                                               final String tableName,
                                               final String indexName,
                                               final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code timestamp without time zone} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableTimestamp(final PgContext ctx,
                                                       final String tableName,
                                                       final String indexName,
                                                       final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code timestamp with time zone} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofTimestamptz(final PgContext ctx,
                                                 final String tableName,
                                                 final String indexName,
                                                 final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code timestamp with time zone} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableTimestamptz(final PgContext ctx,
                                                         final String tableName,
                                                         final String indexName,
                                                         final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one not-null column of {@code uuid} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofUuid(final PgContext ctx,
                                          final String tableName,
                                          final String indexName,
                                          final String columnName) {
        return ofNotNull(ctx, tableName, indexName, columnName, PgColumnType.UUID.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object of zero size with one nullable column of {@code uuid} type and given context.
     *
     * @param ctx        the schema context to enrich table and index name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param indexName  index name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code IndexWithColumns}
     * @since 0.60.0
     */
    public static IndexWithColumns ofNullableUuid(final PgContext ctx,
                                                  final String tableName,
                                                  final String indexName,
                                                  final String columnName) {
        return ofNullable(ctx, tableName, indexName, columnName, PgColumnType.UUID.getColumnType());
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given columns.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          ordered list of columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     */
    public static IndexWithColumns of(final String tableName,
                                      final String indexName,
                                      final long indexSizeInBytes,
                                      final List<Column> columns) {
        return of(Index.of(tableName, indexName, indexSizeInBytes), columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given columns and context.
     *
     * @param ctx              the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param columns          ordered list of columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.14.3
     */
    public static IndexWithColumns of(final PgContext ctx,
                                      final String tableName,
                                      final String indexName,
                                      final long indexSizeInBytes,
                                      final List<Column> columns) {
        return of(Index.of(ctx, tableName, indexName, indexSizeInBytes), columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with zero size, given columns and context.
     *
     * @param ctx       the schema context to enrich table and index name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @param columns   ordered list of columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.20.2
     */
    public static IndexWithColumns of(final PgContext ctx,
                                      final String tableName,
                                      final String indexName,
                                      final List<Column> columns) {
        return of(ctx, tableName, indexName, 0L, columns);
    }

    /**
     * Constructs an {@code IndexWithColumns} object with given index and columns.
     *
     * @param index   index; must be non-null.
     * @param columns ordered list of columns in index; must be non-null.
     * @return {@code IndexWithColumns}
     * @since 0.15.0
     */
    public static IndexWithColumns of(final Index index,
                                      final List<Column> columns) {
        return new IndexWithColumns(index, columns);
    }
}
