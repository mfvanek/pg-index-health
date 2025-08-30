/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import java.util.Objects;

/**
 * An immutable representation of column with type in database table.
 *
 * @author Ivan Vakhrushev
 * @see Column
 * @since 0.20.3
 */
public final class ColumnWithType extends AbstractColumnAware implements Comparable<ColumnWithType> {

    private ColumnWithType(final Column column,
                           final String columnType) {
        super(column, columnType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ColumnWithType.class.getSimpleName() + '{' +
            innerToString() +
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

        if (!(other instanceof final ColumnWithType that)) {
            return false;
        }

        return Objects.equals(column, that.column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ColumnWithType other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!column.equals(other.column)) {
            return column.compareTo(other.column);
        }
        return columnType.compareTo(other.columnType);
    }

    /**
     * Constructs a {@code ColumnWithType} object of a given column and type.
     *
     * @param column     column; should be non-null.
     * @param columnType column type; should be non-blank.
     * @return {@code ColumnWithType}
     */
    public static ColumnWithType of(final Column column,
                                    final String columnType) {
        return new ColumnWithType(column, columnType);
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "integer" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "integer" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofInteger(final Column column) {
        return of(column, "integer");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "smallint" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "smallint" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofSmallint(final Column column) {
        return of(column, "smallint");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "bigint" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "bigint" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofBigint(final Column column) {
        return of(column, "bigint");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "character varying" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "character varying" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofVarchar(final Column column) {
        return of(column, "character varying");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "text" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "text" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofText(final Column column) {
        return of(column, "text");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "timestamp without time zone" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "timestamp without time zone" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofTimestamp(final Column column) {
        return of(column, "timestamp without time zone");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "timestamp with time zone" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "timestamp with time zone" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofTimestamptz(final Column column) {
        return of(column, "timestamp with time zone");
    }

    /**
     * Constructs a {@code ColumnWithType} object with the "uuid" type for the specified column.
     *
     * @param column the column for which the type is being specified; must be non-null.
     * @return a {@code ColumnWithType} object with the "uuid" type.
     * @see <a href="https://www.postgresql.org/docs/current/datatype.html">Data Types</a>
     */
    public static ColumnWithType ofUuid(final Column column) {
        return of(column, "uuid");
    }
}
