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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a column in database table/index/foreign key.
 * Column always belongs to a table/index/foreign key.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
public final class Column implements DbObject, ColumnNameAware, Comparable<Column> {

    private final String tableName;
    private final String columnName;
    private final boolean notNull;

    /**
     * Constructs a {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @param notNull    whether column is not null or nullable
     */
    private Column(final String tableName,
                   final String columnName,
                   final boolean notNull) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
        this.notNull = notNull;
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
     */
    @Override
    public String toString() {
        return Column.class.getSimpleName() + "{tableName='" + tableName + '\'' +
            ", columnName='" + columnName + '\'' +
            ", notNull=" + notNull + '}';
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

        return notNull == that.notNull &&
            Objects.equals(tableName, that.tableName) &&
            Objects.equals(columnName, that.columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableName, columnName, notNull);
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
        if (!columnName.equals(other.columnName)) {
            return columnName.compareTo(other.columnName);
        }
        return Boolean.compare(notNull, other.notNull);
    }

    /**
     * Constructs a not null {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     */
    public static Column ofNotNull(final String tableName,
                                   final String columnName) {
        return new Column(tableName, columnName, true);
    }

    /**
     * Constructs a not null {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.14.3
     */
    public static Column ofNotNull(final PgContext pgContext,
                                   final String tableName,
                                   final String columnName) {
        return ofNotNull(PgContext.enrichWith(tableName, pgContext), columnName);
    }

    /**
     * Constructs a nullable {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     */
    public static Column ofNullable(final String tableName,
                                    final String columnName) {
        return new Column(tableName, columnName, false);
    }

    /**
     * Constructs a nullable {@code Column} object with given context.
     *
     * @param pgContext  the schema context to enrich table name; must be non-null.
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     * @since 0.14.3
     */
    public static Column ofNullable(final PgContext pgContext,
                                    final String tableName,
                                    final String columnName) {
        return ofNullable(PgContext.enrichWith(tableName, pgContext), columnName);
    }
}
