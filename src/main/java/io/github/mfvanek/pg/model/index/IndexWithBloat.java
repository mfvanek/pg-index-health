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

import io.github.mfvanek.pg.model.BloatAware;
import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public class IndexWithBloat extends IndexWithSize implements BloatAware {

    private final long bloatSizeInBytes;
    private final int bloatPercentage;

    private IndexWithBloat(@Nonnull final String tableName,
                           @Nonnull final String indexName,
                           final long indexSizeInBytes,
                           final long bloatSizeInBytes,
                           final int bloatPercentage) {
        super(tableName, indexName, indexSizeInBytes);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.argumentNotNegative(bloatPercentage, "bloatPercentage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBloatSizeInBytes() {
        return bloatSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBloatPercentage() {
        return bloatPercentage;
    }

    @Override
    protected String innerToString() {
        return super.innerToString() + ", bloatSizeInBytes=" + bloatSizeInBytes +
                ", bloatPercentage=" + bloatPercentage;
    }

    @Override
    public String toString() {
        return IndexWithBloat.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * Constructs a {@code IndexWithBloat} object.
     *
     * @param tableName        table name; should be non blank.
     * @param indexName        index name; should be non blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code IndexWithBloat}
     */
    @Nonnull
    public static IndexWithBloat of(@Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final int bloatPercentage) {
        return new IndexWithBloat(tableName, indexName, indexSizeInBytes, bloatSizeInBytes, bloatPercentage);
    }
}
