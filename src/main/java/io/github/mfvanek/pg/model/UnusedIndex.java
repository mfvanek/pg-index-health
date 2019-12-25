/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public final class UnusedIndex extends IndexWithSize {

    private final long indexScans;

    private UnusedIndex(@Nonnull String tableName,
                        @Nonnull String indexName,
                        long indexSizeInBytes,
                        long indexScans) {
        super(tableName, indexName, indexSizeInBytes);
        this.indexScans = Validators.countNotNegative(indexScans, "indexScans");
    }

    public long getIndexScans() {
        return indexScans;
    }

    @Override
    public String toString() {
        return UnusedIndex.class.getSimpleName() + "{" +
                innerToString() +
                ", indexScans=" + indexScans +
                "}";
    }

    public static UnusedIndex of(@Nonnull String tableName,
                                 @Nonnull String indexName,
                                 long indexSizeInBytes,
                                 long indexScans) {
        return new UnusedIndex(tableName, indexName, indexSizeInBytes, indexScans);
    }
}
