/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Normally, indexes should be used primarily when accessing a table.
 * If there are few or no indexes in the table, then seqScans will be larger than indexScans.
 */
public class TableWithMissingIndex extends Table implements Comparable<TableWithMissingIndex> {

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

    @Override
    public String toString() {
        return TableWithMissingIndex.class.getSimpleName() + '{' +
                innerToString() +
                ", seqScans=" + seqScans +
                ", indexScans=" + indexScans +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableWithMissingIndex that = (TableWithMissingIndex) o;
        return getTableName().equals(that.getTableName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTableName());
    }

    @Override
    public int compareTo(TableWithMissingIndex other) {
        return StringUtils.compare(this.getTableName(), other.getTableName());
    }

    public static TableWithMissingIndex of(@Nonnull final String tableName,
                                           final long tableSizeInBytes,
                                           final long seqScans,
                                           final long indexScans) {
        return new TableWithMissingIndex(tableName, tableSizeInBytes, seqScans, indexScans);
    }
}
