/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;

/**
 * An immutable representation of a database table with its columns.
 * Table can have zero or more columns.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public final class TableWithColumns extends AbstractTableAware implements ColumnsAware, Comparable<TableWithColumns> {

    private final List<Column> columns;

    private TableWithColumns(final Table table,
                             final List<Column> columns) {
        super(table);
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, COLUMNS_FIELD + " cannot be null"));
        Validators.validateThatTableIsTheSame(table.getTableName(), defensiveCopy);
        this.columns = defensiveCopy;
    }

    /**
     * Retrieves columns of table (zero or more).
     *
     * @return columns of the table
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
        return TableWithColumns.class.getSimpleName() + '{' +
            table.innerToString() +
            ", " + COLUMNS_FIELD + '=' + columns + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final TableWithColumns that)) {
            return false;
        }

        return Objects.equals(table, that.table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(table);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final TableWithColumns other) {
        Objects.requireNonNull(other, "other cannot be null");
        return table.compareTo(other.table);
    }

    /**
     * Creates a {@code TableWithColumns} instance with no columns.
     *
     * @param table the table reference
     * @return a {@code TableWithColumns} with an empty column list
     */
    public static TableWithColumns withoutColumns(final Table table) {
        return new TableWithColumns(table, List.of());
    }

    /**
     * Creates a {@code TableWithColumns} instance with no columns from the given context and table name.
     *
     * @param ctx       the schema context to enrich table name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @return a {@code TableWithColumns} with no columns
     */
    public static TableWithColumns withoutColumns(final PgContext ctx,
                                                  final String tableName) {
        return new TableWithColumns(Table.of(ctx, tableName), List.of());
    }

    /**
     * Creates a {@code TableWithColumns} from the given table and list of columns.
     *
     * @param table   the table
     * @param columns the list of columns (must not be null)
     * @return a {@code TableWithColumns} instance
     */
    public static TableWithColumns of(final Table table,
                                      final List<Column> columns) {
        return new TableWithColumns(table, columns);
    }

    /**
     * Creates a {@code TableWithColumns} from the given table and columns.
     *
     * @param table        the table
     * @param column       the first column; must be non-null.
     * @param otherColumns other columns; must not contain null elements.
     * @return a {@code TableWithColumns} instance
     * @since 0.51.0
     */
    public static TableWithColumns of(final Table table,
                                      final Column column,
                                      final Column... otherColumns) {
        return of(table, Validators.columnsNotNull(column, otherColumns));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNotNull(final PgContext ctx,
                                             final String tableName,
                                             final String columnName,
                                             final String columnType) {
        return of(Table.of(ctx, tableName), Column.ofNotNull(ctx, tableName, columnName, columnType));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNotNull(final PgContext ctx,
                                             final String tableName,
                                             final String columnName,
                                             final PgColumnType columnType) {
        return ofNotNull(ctx, tableName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullable(final PgContext ctx,
                                              final String tableName,
                                              final String columnName,
                                              final String columnType) {
        return of(Table.of(ctx, tableName), Column.ofNullable(ctx, tableName, columnName, columnType));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullable(final PgContext ctx,
                                              final String tableName,
                                              final String columnName,
                                              final PgColumnType columnType) {
        return ofNullable(ctx, tableName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code bigint} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofBigint(final PgContext ctx,
                                            final String tableName,
                                            final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code bigint} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableBigint(final PgContext ctx,
                                                    final String tableName,
                                                    final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code integer} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofInteger(final PgContext ctx,
                                             final String tableName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code integer} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableInteger(final PgContext ctx,
                                                     final String tableName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code character varying} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofVarchar(final PgContext ctx,
                                             final String tableName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code character varying} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableVarchar(final PgContext ctx,
                                                     final String tableName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code text} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofText(final PgContext ctx,
                                          final String tableName,
                                          final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code text} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableText(final PgContext ctx,
                                                  final String tableName,
                                                  final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code character} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofCharacter(final PgContext ctx,
                                               final String tableName,
                                               final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code character} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableCharacter(final PgContext ctx,
                                                       final String tableName,
                                                       final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code boolean} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofBoolean(final PgContext ctx,
                                             final String tableName,
                                             final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code boolean} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableBoolean(final PgContext ctx,
                                                     final String tableName,
                                                     final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code timestamp without time zone} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofTimestamp(final PgContext ctx,
                                               final String tableName,
                                               final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code timestamp without time zone} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableTimestamp(final PgContext ctx,
                                                       final String tableName,
                                                       final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code timestamp with time zone} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofTimestamptz(final PgContext ctx,
                                                 final String tableName,
                                                 final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code timestamp with time zone} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableTimestamptz(final PgContext ctx,
                                                         final String tableName,
                                                         final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column of {@code uuid} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofUuid(final PgContext ctx,
                                          final String tableName,
                                          final String columnName) {
        return ofNotNull(ctx, tableName, columnName, PgColumnType.UUID.getColumnType());
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column of {@code uuid} type.
     *
     * @param ctx        the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     * @since 0.51.0
     */
    public static TableWithColumns ofNullableUuid(final PgContext ctx,
                                                  final String tableName,
                                                  final String columnName) {
        return ofNullable(ctx, tableName, columnName, PgColumnType.UUID.getColumnType());
    }
}
