/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database table with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public final class TableWithBloat extends AbstractTableAware implements BloatAware, Comparable<TableWithBloat> {

    private final long bloatSizeInBytes;
    private final double bloatPercentage;

    private TableWithBloat(@Nonnull final Table table,
                           final long bloatSizeInBytes,
                           final double bloatPercentage) {
        super(table);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.validPercent(bloatPercentage, "bloatPercentage");
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
    @Nonnull
    @Override
    public String toString() {
        return TableWithBloat.class.getSimpleName() + '{' +
            table.innerToString() +
            ", bloatSizeInBytes=" + bloatSizeInBytes +
            ", bloatPercentage=" + bloatPercentage + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TableWithBloat)) {
            return false;
        }

        final TableWithBloat that = (TableWithBloat) other;
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
    public int compareTo(@Nonnull final TableWithBloat other) {
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
    @Nonnull
    public static TableWithBloat of(@Nonnull final String tableName,
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
    @Nonnull
    public static TableWithBloat of(@Nonnull final PgContext pgContext,
                                    @Nonnull final String tableName,
                                    final long tableSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        final Table table = Table.of(pgContext, tableName, tableSizeInBytes);
        return of(table, bloatSizeInBytes, bloatPercentage);
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
    @Nonnull
    public static TableWithBloat of(@Nonnull final Table table,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return new TableWithBloat(table, bloatSizeInBytes, bloatPercentage);
    }
}
