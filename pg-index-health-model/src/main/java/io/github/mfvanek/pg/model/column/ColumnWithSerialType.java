/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * An immutable representation of column with serial type in database table.
 * The serial type is taken from the column type, which must be one of
 * {@link PgColumnType#SMALL_SERIAL}, {@link PgColumnType#SERIAL} or {@link PgColumnType#BIG_SERIAL}.
 *
 * @author Ivan Vakhrushev
 * @see PgColumnType
 * @see Column
 * @since 0.6.2
 */
public final class ColumnWithSerialType implements DbObject, ColumnNameAware, SequenceNameAware, Comparable<ColumnWithSerialType> {

    private static final Set<PgColumnType> SERIAL_TYPES = EnumSet.of(PgColumnType.SMALL_SERIAL, PgColumnType.SERIAL, PgColumnType.BIG_SERIAL);

    private final Column column;
    private final String sequenceName;

    private ColumnWithSerialType(final Column column,
                                 final String sequenceName) {
        this.column = Objects.requireNonNull(column, COLUMN_FIELD + " cannot be null");
        validateSerialType(column.getColumnType());
        this.sequenceName = Validators.notBlank(sequenceName, SEQUENCE_NAME_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return column.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return column.getObjectType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return column.getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName() {
        return column.getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotNull() {
        return column.isNotNull();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnType() {
        return column.getColumnType();
    }

    /**
     * Retrieves the current instance as a {@code Column}.
     *
     * @return the {@code Column} associated with this instance
     * @since 0.20.3
     */
    public Column toColumn() {
        return column;
    }

    /**
     * Retrieves serial type of the column.
     *
     * @return serial type of the column: {@link PgColumnType#SMALL_SERIAL}, {@link PgColumnType#SERIAL} or {@link PgColumnType#BIG_SERIAL}
     */
    public PgColumnType getSerialType() {
        return PgColumnType.valueFrom(column.getColumnType());
    }

    /**
     * Retrieves the name of the associated sequence.
     *
     * @return name of the associated sequence
     */
    @Override
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ColumnWithSerialType.class.getSimpleName() + '{' +
            COLUMN_FIELD + '=' + column +
            ", " + SEQUENCE_NAME_FIELD + "='" + sequenceName + '\'' +
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

        if (!(other instanceof final ColumnWithSerialType that)) {
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
    public int compareTo(final ColumnWithSerialType other) {
        Objects.requireNonNull(other, "other cannot be null");
        return column.compareTo(other.column);
    }

    private static void validateSerialType(final String columnType) {
        if (!SERIAL_TYPES.contains(PgColumnType.valueFrom(columnType))) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "column type must be serial but was '%s'", columnType));
        }
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object.
     *
     * @param column       column with serial type; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType of(final Column column,
                                          final String sequenceName) {
        return new ColumnWithSerialType(column, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object with given context.
     *
     * @param ctx          the schema context to enrich sequence name; must be non-null.
     * @param column       column with serial type; should be non-null.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType of(final PgContext ctx,
                                          final Column column,
                                          final String sequenceName) {
        return of(column, PgContext.enrichWith(sequenceName, ctx));
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object with given context.
     * The column is created from the given table name, column name, nullability and column type.
     *
     * @param ctx          the schema context to enrich table name and sequence name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param columnName   column name; should be non-blank.
     * @param notNull      whether the column is not null.
     * @param columnType   column type; must be one of {@code smallserial}, {@code serial} or {@code bigserial}.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType of(final PgContext ctx,
                                          final String tableName,
                                          final String columnName,
                                          final boolean notNull,
                                          final String columnType,
                                          final String sequenceName) {
        return of(ctx, Column.of(ctx, tableName, columnName, notNull, columnType), sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object with given context.
     * The column is created from the given table name, column name, nullability and column type.
     *
     * @param ctx          the schema context to enrich table name and sequence name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param columnName   column name; should be non-blank.
     * @param notNull      whether the column is not null.
     * @param columnType   column type; must be one of {@link PgColumnType#SMALL_SERIAL}, {@link PgColumnType#SERIAL} or {@link PgColumnType#BIG_SERIAL}.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType of(final PgContext ctx,
                                          final String tableName,
                                          final String columnName,
                                          final boolean notNull,
                                          final PgColumnType columnType,
                                          final String sequenceName) {
        return of(ctx, tableName, columnName, notNull, Validators.columnTypeNotNull(columnType), sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code bigserial} type with a not null column and given context.
     *
     * @param ctx          the schema context to enrich table name and sequence name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param columnName   column name; should be non-blank.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType ofBigSerial(final PgContext ctx,
                                                   final String tableName,
                                                   final String columnName,
                                                   final String sequenceName) {
        return of(ctx, tableName, columnName, true, PgColumnType.BIG_SERIAL, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code serial} type with a not null column and given context.
     *
     * @param ctx          the schema context to enrich table name and sequence name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param columnName   column name; should be non-blank.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType ofSerial(final PgContext ctx,
                                                final String tableName,
                                                final String columnName,
                                                final String sequenceName) {
        return of(ctx, tableName, columnName, true, PgColumnType.SERIAL, sequenceName);
    }

    /**
     * Constructs a {@code ColumnWithSerialType} object of {@code smallserial} type with a not null column and given context.
     *
     * @param ctx          the schema context to enrich table name and sequence name; must be non-null.
     * @param tableName    table name; should be non-blank.
     * @param columnName   column name; should be non-blank.
     * @param sequenceName sequence name; should be non-blank.
     * @return {@code ColumnWithSerialType}
     * @since 0.60.0
     */
    public static ColumnWithSerialType ofSmallSerial(final PgContext ctx,
                                                     final String tableName,
                                                     final String columnName,
                                                     final String sequenceName) {
        return of(ctx, tableName, columnName, true, PgColumnType.SMALL_SERIAL, sequenceName);
    }
}
