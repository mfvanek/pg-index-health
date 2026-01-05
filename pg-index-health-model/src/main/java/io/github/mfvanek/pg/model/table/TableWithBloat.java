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

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a database table with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
public final class TableWithBloat extends AbstractTableAware implements BloatAware, Comparable<TableWithBloat> {

    private final long bloatSizeInBytes;
    private final double bloatPercentage;

    private TableWithBloat(final Table table,
                           final long bloatSizeInBytes,
                           final double bloatPercentage) {
        super(table);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, BLOAT_SIZE_IN_BYTES_FIELD);
        this.bloatPercentage = Validators.validPercent(bloatPercentage, BLOAT_PERCENTAGE_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBloatSizeInBytes() {
        return bloatSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getBloatPercentage() {
        return bloatPercentage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TableWithBloat.class.getSimpleName() + '{' +
            table.innerToString() +
            ", " + BLOAT_SIZE_IN_BYTES_FIELD + '=' + bloatSizeInBytes +
            ", " + BLOAT_PERCENTAGE_FIELD + '=' + bloatPercentage + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final TableWithBloat that)) {
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
    public int compareTo(final TableWithBloat other) {
        Objects.requireNonNull(other, "other cannot be null");
        return table.compareTo(other.table);
    }

    /**
     * Constructs a {@code TableWithBloat} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code TableWithBloat}
     */
    public static TableWithBloat of(final String tableName,
                                    final long tableSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        final Table table = Table.of(tableName, tableSizeInBytes);
        return of(table, bloatSizeInBytes, bloatPercentage);
    }

    /**
     * Constructs a {@code TableWithBloat} object with given context.
     *
     * @param pgContext        the schema context to enrich table name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code TableWithBloat}
     * @since 0.14.3
     */
    public static TableWithBloat of(final PgContext pgContext,
                                    final String tableName,
                                    final long tableSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        final Table table = Table.of(pgContext, tableName, tableSizeInBytes);
        return of(table, bloatSizeInBytes, bloatPercentage);
    }

    /**
     * Constructs a {@code TableWithBloat} object with given context and zero bloat.
     *
     * @param pgContext the schema context to enrich table name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @return {@code TableWithBloat}
     * @since 0.14.3
     */
    public static TableWithBloat of(final PgContext pgContext,
                                    final String tableName) {
        return of(pgContext, tableName, 0L, 0L, 0.0);
    }

    /**
     * Constructs a {@code TableWithBloat} object.
     *
     * @param table            table; should be non-null.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code TableWithBloat}
     * @since 0.7.0
     */
    public static TableWithBloat of(final Table table,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return new TableWithBloat(table, bloatSizeInBytes, bloatPercentage);
    }
}
