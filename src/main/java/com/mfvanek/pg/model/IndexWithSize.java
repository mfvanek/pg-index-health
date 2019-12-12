/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import com.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public class IndexWithSize extends Index implements IndexSizeAware {

    private final long indexSizeInBytes;

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
        return IndexWithSize.class.getSimpleName() + "{" +
                innerToString() +
                "}";
    }

    public static IndexWithSize of(@Nonnull String tableName,
                                   @Nonnull String indexName,
                                   long indexSizeInBytes) {
        return new IndexWithSize(tableName, indexName, indexSizeInBytes);
    }
}
