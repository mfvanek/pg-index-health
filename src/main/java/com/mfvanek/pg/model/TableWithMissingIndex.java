/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import com.mfvanek.pg.utils.Validators;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Normally, indesex should be used primarily when accessing a table.
 * If there are few or no indexes in the table, then seqScans will be larger than indexScans.
 */
public class TableWithMissingIndex implements TableAware, Comparable<TableWithMissingIndex> {

    private final String tableName;
    private final long seqScans;
    private final long indexScans;

    private TableWithMissingIndex(@Nonnull String tableName, long seqScans, long indexScans) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.seqScans = Validators.countNotNegative(seqScans, "seqScans");
        this.indexScans = Validators.countNotNegative(indexScans, "indexScans");
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    public long getSeqScans() {
        return seqScans;
    }

    public long getIndexScans() {
        return indexScans;
    }

    @Override
    public String toString() {
        return TableWithMissingIndex.class.getSimpleName() + "{" +
                "tableName=\'" + tableName + "\'" +
                ", seqScans=" + seqScans +
                ", indexScans=" + indexScans +
                "}";
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
        return tableName.equals(that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }

    @Override
    public int compareTo(TableWithMissingIndex other) {
        return StringUtils.compare(this.tableName, other.tableName);
    }

    public static TableWithMissingIndex of(@Nonnull String tableName, long seqScans, long indexScans) {
        return new TableWithMissingIndex(tableName, seqScans, indexScans);
    }
}
