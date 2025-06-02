/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import io.github.mfvanek.pg.model.column.ColumnsAware;
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
        final List<Column> defensiveCopy = List.copyOf(Objects.requireNonNull(columns, "columns cannot be null"));
        Validators.validateThatTableIsTheSame(table.getTableName(), defensiveCopy);
        this.columns = defensiveCopy;
    }

    /**
     * Retrieves columns of table (zero or more).
     *
     * @return columns of table
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
            ", columns=" + columns + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TableWithColumns)) {
            return false;
        }

        final TableWithColumns that = (TableWithColumns) other;
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
     * @param pgContext the schema context to enrich table name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @return a {@code TableWithColumns} with no columns
     */
    public static TableWithColumns withoutColumns(final PgContext pgContext,
                                                  final String tableName) {
        return new TableWithColumns(Table.of(pgContext, tableName), List.of());
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
     * Creates a {@code TableWithColumns} containing a single column.
     *
     * @param table  the table
     * @param column the single column
     * @return a {@code TableWithColumns} with one column
     */
    public static TableWithColumns ofSingle(final Table table,
                                            final Column column) {
        return of(table, List.of(column));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single non-nullable column.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one non-nullable column
     */
    public static TableWithColumns ofNotNullColumn(final PgContext pgContext,
                                                   final String tableName,
                                                   final String columnName) {
        return ofSingle(Table.of(pgContext, tableName), Column.ofNotNull(pgContext, tableName, columnName));
    }

    /**
     * Creates a {@code TableWithColumns} for the given table and a single nullable column.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return a {@code TableWithColumns} with one nullable column
     */
    public static TableWithColumns ofNullableColumn(final PgContext pgContext,
                                                    final String tableName,
                                                    final String columnName) {
        return ofSingle(Table.of(pgContext, tableName), Column.ofNullable(pgContext, tableName, columnName));
    }
}
