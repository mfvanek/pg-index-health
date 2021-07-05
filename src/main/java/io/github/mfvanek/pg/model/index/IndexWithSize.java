/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public class IndexWithSize extends Index implements IndexSizeAware {

    private final long indexSizeInBytes;

    @SuppressWarnings("WeakerAccess")
    protected IndexWithSize(@Nonnull final String tableName,
                            @Nonnull final String indexName,
                            final long indexSizeInBytes) {
        super(tableName, indexName);
        this.indexSizeInBytes = Validators.sizeNotNegative(indexSizeInBytes, "indexSizeInBytes");
    }

    /**
     * {@inheritDoc}
     */
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

    public static IndexWithSize of(@Nonnull final String tableName,
                                   @Nonnull final String indexName,
                                   final long indexSizeInBytes) {
        return new IndexWithSize(tableName, indexName, indexSizeInBytes);
    }
}
