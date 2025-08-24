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
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a table in a database with additional information on reads amount via index or sequential scans.
 *
 * @author Ivan Vakhrushev
 */
public final class TableWithMissingIndex extends AbstractTableAware implements Comparable<TableWithMissingIndex> {

    /**
     * Represents the field name used to store information about sequential scans.
     */
    public static final String SEQ_SCANS_FIELD = "seqScans";
    /**
     * Represents the field name used to store information about index scans.
     */
    public static final String INDEX_SCANS_FIELD = "indexScans";

    /**
     * The number of sequential scans performed on the table.
     * Normally, indexes should be used primarily when accessing a table.
     * If there are few or no indexes in the table, then seqScans will be larger than indexScans.
     */
    private final long seqScans;

    /**
     * The number of index scans performed on the table.
     */
    private final long indexScans;

    private TableWithMissingIndex(final Table table,
                                  final long seqScans,
                                  final long indexScans) {
        super(table);
        this.seqScans = Validators.countNotNegative(seqScans, SEQ_SCANS_FIELD);
        this.indexScans = Validators.countNotNegative(indexScans, INDEX_SCANS_FIELD);
    }

    /**
     * Retrieves the number of sequential scans performed on this table.
     *
     * @return the sequential scan count
     */
    public long getSeqScans() {
        return seqScans;
    }

    /**
     * Retrieves the number of index scans performed on this table.
     *
     * @return the index scan count
     */
    public long getIndexScans() {
        return indexScans;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return TableWithMissingIndex.class.getSimpleName() + '{' +
            table.innerToString() +
            ", " + SEQ_SCANS_FIELD + '=' + seqScans +
            ", " + INDEX_SCANS_FIELD + '=' + indexScans +
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

        if (!(other instanceof final TableWithMissingIndex that)) {
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
    public int compareTo(final TableWithMissingIndex other) {
        Objects.requireNonNull(other, "other cannot be null");
        return table.compareTo(other.table);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param seqScans         the number of sequential scans initiated on this table; should be non-negative.
     * @param indexScans       the number of index scans initiated on this table; should be non-negative.
     * @return {@code TableWithMissingIndex}
     */
    public static TableWithMissingIndex of(final String tableName,
                                           final long tableSizeInBytes,
                                           final long seqScans,
                                           final long indexScans) {
        final Table table = Table.of(tableName, tableSizeInBytes);
        return of(table, seqScans, indexScans);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object with zero size.
     *
     * @param tableName table name; should be non-blank.
     * @return {@code TableWithMissingIndex}
     * @since 0.15.0
     */
    public static TableWithMissingIndex of(final String tableName) {
        return of(tableName, 0L, 0L, 0L);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object with given context.
     *
     * @param pgContext        the schema context to enrich table name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param tableSizeInBytes table size in bytes; should be positive or zero.
     * @param seqScans         the number of sequential scans initiated on this table; should be non-negative.
     * @param indexScans       the number of index scans initiated on this table; should be non-negative.
     * @return {@code TableWithMissingIndex}
     * @since 0.14.3
     */
    public static TableWithMissingIndex of(final PgContext pgContext,
                                           final String tableName,
                                           final long tableSizeInBytes,
                                           final long seqScans,
                                           final long indexScans) {
        final Table table = Table.of(pgContext, tableName, tableSizeInBytes);
        return of(table, seqScans, indexScans);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object with given context and zero size.
     *
     * @param pgContext the schema context to enrich table name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @return {@code TableWithMissingIndex}
     * @since 0.15.0
     */
    public static TableWithMissingIndex of(final PgContext pgContext,
                                           final String tableName) {
        return of(pgContext, tableName, 0L, 0L, 0L);
    }

    /**
     * Constructs a {@code TableWithMissingIndex} object.
     *
     * @param table      table; should be non-null.
     * @param seqScans   the number of sequential scans initiated on this table; should be non-negative.
     * @param indexScans the number of index scans initiated on this table; should be non-negative.
     * @return {@code TableWithMissingIndex}
     * @since 0.7.0
     */
    public static TableWithMissingIndex of(final Table table,
                                           final long seqScans,
                                           final long indexScans) {
        return new TableWithMissingIndex(table, seqScans, indexScans);
    }
}
