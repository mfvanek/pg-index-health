/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.utils.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Representation of a table in a database with additional information on reads amount via index or sequential scans.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public class TableWithMissingIndex extends AbstractTableAware implements Comparable<TableWithMissingIndex> {

    // Normally, indexes should be used primarily when accessing a table.
    // If there are few or no indexes in the table, then seqScans will be larger than indexScans.
    private final long seqScans;
    private final long indexScans;

    private TableWithMissingIndex(@Nonnull final Table table,
                                  final long seqScans,
                                  final long indexScans) {
        super(table);
        this.seqScans = Validators.countNotNegative(seqScans, "seqScans");
        this.indexScans = Validators.countNotNegative(indexScans, "indexScans");
    }

    public long getSeqScans() {
        return seqScans;
    }

    public long getIndexScans() {
        return indexScans;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return TableWithMissingIndex.class.getSimpleName() + '{' +
                table.innerToString() +
                ", seqScans=" + seqScans +
                ", indexScans=" + indexScans +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof TableWithMissingIndex)) {
            return false;
        }

        final TableWithMissingIndex that = (TableWithMissingIndex) other;
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
    public int compareTo(@Nonnull final TableWithMissingIndex other) {
        Objects.requireNonNull(other, "other cannot be null");
        return table.compareTo(other.table);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param seqScans         number of sequential scans initiated on this table; should be non-negative.
     * @param indexScans       number of index scans initiated on this table; should be non-negative.
     * @return {@code TableWithMissingIndex}
     */
    public static TableWithMissingIndex of(@Nonnull final String tableName,
                                           final long tableSizeInBytes,
                                           final long seqScans,
                                           final long indexScans) {
        final Table table = Table.of(tableName, tableSizeInBytes);
        return of(table, seqScans, indexScans);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object.
     *
     * @param table      table; should be non-null.
     * @param seqScans   number of sequential scans initiated on this table; should be non-negative.
     * @param indexScans number of index scans initiated on this table; should be non-negative.
     * @return {@code TableWithMissingIndex}
     * @since 0.7.0
     */
    public static TableWithMissingIndex of(@Nonnull final Table table,
                                           final long seqScans,
                                           final long indexScans) {
        return new TableWithMissingIndex(table, seqScans, indexScans);
    }
}
