/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of column in database table.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
@Immutable
public class Column implements DbObject, ColumnNameAware, Comparable<Column> {

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
    protected Column(@Nonnull final String tableName,
                     @Nonnull final String columnName,
                     final boolean notNull) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.columnName = Validators.notBlank(columnName, "columnName");
        this.notNull = notNull;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return getColumnName();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final PgObjectType getObjectType() {
        return PgObjectType.TABLE;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getTableName() {
        return tableName;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
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
    @Nonnull
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
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Column)) {
            return false;
        }

        final Column that = (Column) other;
        return notNull == that.notNull &&
            Objects.equals(tableName, that.tableName) &&
            Objects.equals(columnName, that.columnName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(tableName, columnName, notNull);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(@Nonnull final Column other) {
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
    @Nonnull
    public static Column ofNotNull(@Nonnull final String tableName, @Nonnull final String columnName) {
        return new Column(tableName, columnName, true);
    }

    /**
     * Constructs a nullable {@code Column} object.
     *
     * @param tableName  table name; should be non-blank.
     * @param columnName column name; should be non-blank.
     * @return {@code Column}
     */
    @Nonnull
    public static Column ofNullable(@Nonnull final String tableName, @Nonnull final String columnName) {
        return new Column(tableName, columnName, false);
    }
}
