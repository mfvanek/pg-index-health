/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import java.util.Objects;

public class IndexWithSize extends Index implements IndexSizeAware {

    private final long indexSizeInBytes;

    @SuppressWarnings("WeakerAccess")
    protected IndexWithSize(@Nonnull String tableName,
                            @Nonnull String indexName,
                            long indexSizeInBytes) {
        super(tableName, indexName);
        this.indexSizeInBytes = Validators.sizeNotNegative(indexSizeInBytes, "indexSizeInBytes");
    }

    @Override
    public long getIndexSizeInBytes() {
        return indexSizeInBytes;
    }

    @Override
    protected String innerToString() {
        return super.innerToString() + ", indexSizeInBytes=" + indexSizeInBytes;
    }

    @Override
    public String toString() {
        return IndexWithSize.class.getSimpleName() + '{' + innerToString() + '}';
    }

    public static IndexWithSize of(@Nonnull String tableName,
                                   @Nonnull String indexName,
                                   long indexSizeInBytes) {
        return new IndexWithSize(tableName, indexName, indexSizeInBytes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        IndexWithSize that = (IndexWithSize) o;
        return indexSizeInBytes == that.indexSizeInBytes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indexSizeInBytes);
    }
}
