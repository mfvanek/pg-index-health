/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Representation of a table in a database with additional information on reads amount via index or sequential scans.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public class TableWithMissingIndex extends Table {

    // Normally, indexes should be used primarily when accessing a table.
    // If there are few or no indexes in the table, then seqScans will be larger than indexScans.
    private final long seqScans;
    private final long indexScans;

    private TableWithMissingIndex(@Nonnull final String tableName,
                                  final long tableSizeInBytes,
                                  final long seqScans,
                                  final long indexScans) {
        super(tableName, tableSizeInBytes);
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
                innerToString() +
                ", seqScans=" + seqScans +
                ", indexScans=" + indexScans +
                '}';
    }

    public static TableWithMissingIndex of(@Nonnull final String tableName,
                                           final long tableSizeInBytes,
                                           final long seqScans,
                                           final long indexScans) {
        return new TableWithMissingIndex(tableName, tableSizeInBytes, seqScans, indexScans);
    }
}
