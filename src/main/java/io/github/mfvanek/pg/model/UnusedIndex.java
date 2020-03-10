/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public final class UnusedIndex extends IndexWithSize {

    private final long indexScans;

    private UnusedIndex(@Nonnull final String tableName,
                        @Nonnull final String indexName,
                        final long indexSizeInBytes,
                        final long indexScans) {
        super(tableName, indexName, indexSizeInBytes);
        this.indexScans = Validators.countNotNegative(indexScans, "indexScans");
    }

    public long getIndexScans() {
        return indexScans;
    }

    @Override
    public String toString() {
        return UnusedIndex.class.getSimpleName() + '{' +
                innerToString() +
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

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static UnusedIndex of(@Nonnull final String tableName,
                                 @Nonnull final String indexName,
                                 final long indexSizeInBytes,
                                 final long indexScans) {
        return new UnusedIndex(tableName, indexName, indexSizeInBytes, indexScans);
    }
}
