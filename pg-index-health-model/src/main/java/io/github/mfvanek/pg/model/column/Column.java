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
public final class Column implements DbObject, ColumnTypeAware, Comparable<Column> {

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
     * Constructs a not null {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNotNull(final String tableName,
                                   final String columnName,
                                   final String columnType) {
        return of(tableName, columnName, true, columnType);
    }

    /**
     * Constructs a not null {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNotNull(final String tableName,
                                   final String columnName,
                                   final PgColumnType columnType) {
        return ofNotNull(tableName, columnName, typeName(columnType));
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
        return ofNotNull(pgContext, tableName, columnName, typeName(columnType));
    }

    /**
     * Constructs a nullable {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; should be non-blank.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullable(final String tableName,
                                    final String columnName,
                                    final String columnType) {
        return of(tableName, columnName, false, columnType);
    }

    /**
     * Constructs a nullable {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param columnType column type; must be non-null.
     * @return {@code Column}
     * @since 0.51.0
     */
    public static Column ofNullable(final String tableName,
                                    final String columnName,
                                    final PgColumnType columnType) {
        return ofNullable(tableName, columnName, typeName(columnType));
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
        return ofNullable(pgContext, tableName, columnName, typeName(columnType));
    }

    private static String typeName(final PgColumnType columnType) {
        return Objects.requireNonNull(columnType, COLUMN_TYPE_FIELD + " cannot be null").getColumnType();
    }
}
