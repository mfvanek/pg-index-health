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
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a column in a database table/index/foreign key.
 * Column always belongs to a table/index/foreign key.
 *
 * @author Ivan Vakhrushev
 * @see PgColumnType
 * @since 0.5.0
 */
public final class Column implements DbObject, ColumnNameAware, Comparable<Column> {

    private final String tableName;
    private final String columnName;
    private final boolean notNull;
    private final String columnType;

    /**
     * Constructs a {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param notNull    whether a column is not null or nullable
     * @param columnType column type; should be non-blank.
     */
    private Column(final String tableName,
                   final String columnName,
                   final boolean notNull,
                   final String columnType) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, COLUMN_NAME_FIELD);
        this.notNull = notNull;
        this.columnType = Validators.notBlank(columnType, COLUMN_TYPE_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.TABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName() {
        return columnName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNotNull() {
        return notNull;
    }

    /**
     * {@inheritDoc}
     *
     * @since 0.51.0
     */
    @Override
    public String getColumnType() {
        return columnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Column.class.getSimpleName() + '{' +
            TABLE_NAME_FIELD + "='" + tableName + '\'' +
            ", " + COLUMN_NAME_FIELD + "='" + columnName + '\'' +
            ", " + NOT_NULL_FIELD + '=' + notNull +
            ", " + COLUMN_TYPE_FIELD + "='" + columnType + '\'' + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final Column that)) {
            return false;
        }

        return Objects.equals(tableName, that.tableName) &&
            Objects.equals(columnName, that.columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableName, columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Column other) {
        Objects.requireNonNull(other, "other cannot be null");
        if (!tableName.equals(other.tableName)) {
            return tableName.compareTo(other.tableName);
        }
        return columnName.compareTo(other.columnName);
    }

    /**
     * Constructs a {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param notNull    whether a column is not null or nullable
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column of(final String tableName,
                            final String columnName,
                            final boolean notNull,
                            final String columnType) {
        return new Column(tableName, columnName, notNull, columnType);
    }

    /**
     * Constructs a {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param notNull    whether a column is not null or nullable
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column of(final PgContext pgContext,
                            final String tableName,
                            final String columnName,
                            final boolean notNull,
                            final String columnType) {
        return of(PgContext.enrichWith(tableName, pgContext), columnName, notNull, columnType);
    }

    /**
     * Constructs a not null {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNotNull(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName,
                                   final String columnType) {
        return of(pgContext, tableName, columnName, true, columnType);
    }

    /**
     * Constructs a not null {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNotNull(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName,
                                   final PgColumnType columnType) {
        return ofNotNull(pgContext, tableName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs a nullable {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullable(final PgContext pgContext,
                                    final String tableName,
                                    final String columnName,
                                    final String columnType) {
        return of(pgContext, tableName, columnName, false, columnType);
    }

    /**
     * Constructs a nullable {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullable(final PgContext pgContext,
                                    final String tableName,
                                    final String columnName,
                                    final PgColumnType columnType) {
        return ofNullable(pgContext, tableName, columnName, Validators.columnTypeNotNull(columnType));
    }

    /**
     * Constructs a not null {@code Column} object of {@code bigint} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofBigint(final PgContext pgContext,
                                  final String tableName,
                                  final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code bigint} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableBigint(final PgContext pgContext,
                                          final String tableName,
                                          final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.BIGINT.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code integer} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofInteger(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code integer} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableInteger(final PgContext pgContext,
                                           final String tableName,
                                           final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.INTEGER.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code character varying} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofVarchar(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code character varying} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableVarchar(final PgContext pgContext,
                                           final String tableName,
                                           final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.VARCHAR.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code text} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofText(final PgContext pgContext,
                                final String tableName,
                                final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code text} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableText(final PgContext pgContext,
                                        final String tableName,
                                        final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.TEXT.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code character} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofCharacter(final PgContext pgContext,
                                     final String tableName,
                                     final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code character} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableCharacter(final PgContext pgContext,
                                             final String tableName,
                                             final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.CHARACTER.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code boolean} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofBoolean(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code boolean} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableBoolean(final PgContext pgContext,
                                           final String tableName,
                                           final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.BOOLEAN.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code timestamp without time zone} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofTimestamp(final PgContext pgContext,
                                     final String tableName,
                                     final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code timestamp without time zone} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableTimestamp(final PgContext pgContext,
                                             final String tableName,
                                             final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.TIMESTAMP.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code timestamp with time zone} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofTimestamptz(final PgContext pgContext,
                                       final String tableName,
                                       final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code timestamp with time zone} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableTimestamptz(final PgContext pgContext,
                                               final String tableName,
                                               final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.TIMESTAMPTZ.getColumnType());
    }

    /**
     * Constructs a not null {@code Column} object of {@code uuid} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofUuid(final PgContext pgContext,
                                final String tableName,
                                final String columnName) {
        return ofNotNull(pgContext, tableName, columnName, PgColumnType.UUID.getColumnType());
    }

    /**
     * Constructs a nullable {@code Column} object of {@code uuid} type with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullableUuid(final PgContext pgContext,
                                        final String tableName,
                                        final String columnName) {
        return ofNullable(pgContext, tableName, columnName, PgColumnType.UUID.getColumnType());
    }
}
