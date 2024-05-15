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
public class TableWithBloat extends AbstractTableAware implements TableBloatAware, Comparable<TableWithBloat> {

    private final long bloatSizeInBytes;
    private final int bloatPercentage;

    private TableWithBloat(@Nonnull final Table table,
                           final long bloatSizeInBytes,
                           final int bloatPercentage) {
        super(table);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.argumentNotNegative(bloatPercentage, "bloatPercentage");
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
    public int getBloatPercentage() {
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
    public final boolean equals(final Object other) {
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
    public final int hashCode() {
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
    public static TableWithBloat of(@Nonnull final String tableName,
                                    final long tableSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final int bloatPercentage) {
        final Table table = Table.of(tableName, tableSizeInBytes);
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
    public static TableWithBloat of(@Nonnull final Table table,
                                    final long bloatSizeInBytes,
                                    final int bloatPercentage) {
        return new TableWithBloat(table, bloatSizeInBytes, bloatPercentage);
    }
}
