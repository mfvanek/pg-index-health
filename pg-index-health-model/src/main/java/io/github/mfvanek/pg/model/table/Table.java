/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a database table.
 *
 * @author Ivan Vakhrushev
 */
public final class Table implements DbObject, TableSizeAware, Comparable<Table> {

    private final String tableName;
    private final long tableSizeInBytes;

    private Table(final String tableName, final long tableSizeInBytes) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.tableSizeInBytes = Validators.sizeNotNegative(tableSizeInBytes, "tableSizeInBytes");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getTableName();
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
    public long getTableSizeInBytes() {
        return tableSizeInBytes;
    }

    /**
     * An auxiliary utility method for implementing {@code toString()} in child classes.
     *
     * @return string representation of the internal fields of this class
     */
    String innerToString() {
        return TABLE_NAME_FIELD + "='" + tableName + '\'' +
            ", tableSizeInBytes=" + tableSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Table.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final Table that)) {
            return false;
        }

        return Objects.equals(tableName, that.tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Table other) {
        Objects.requireNonNull(other, "other cannot be null");
        return tableName.compareTo(other.tableName);
    }

    /**
     * Constructs a {@code Table} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @return {@code Table}
     */
    public static Table of(final String tableName,
                           final long tableSizeInBytes) {
        return new Table(tableName, tableSizeInBytes);
    }

    /**
     * Constructs a {@code Table} object with given context.
     *
     * @param pgContext        the schema context to enrich table name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @return {@code Table}
     * @since 0.14.3
     */
    public static Table of(final PgContext pgContext,
                           final String tableName,
                           final long tableSizeInBytes) {
        return of(PgContext.enrichWith(tableName, pgContext), tableSizeInBytes);
    }

    /**
     * Constructs a {@code Table} object with zero size.
     *
     * @param tableName table name; should be non-blank.
     * @return {@code Table}
     * @since 0.14.3
     */
    public static Table of(final String tableName) {
        return of(tableName, 0L);
    }

    /**
     * Constructs a {@code Table} object with zero size and given context.
     *
     * @param pgContext the schema context to enrich table name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @return {@code Table}
     * @since 0.14.3
     */
    public static Table of(final PgContext pgContext,
                           final String tableName) {
        Objects.requireNonNull(pgContext, "pgContext cannot be null");
        return of(PgContext.enrichWith(tableName, pgContext));
    }
}
