/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.validation.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about size.
 *
 * @author Ivan Vahrushev
 * @since 0.0.1
 */
@Immutable
public class IndexWithSize extends Index implements IndexSizeAware {

    private final long indexSizeInBytes;

    /**
     * Constructs an {@code IndexWithSize} object with the specified table name, index name, and index size.
     *
     * @param tableName        name of the table associated with the index; must be non-blank.
     * @param indexName        name of the index; must be non-blank.
     * @param indexSizeInBytes size of the index in bytes; must be non-negative.
     */
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

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    protected String innerToString() {
        return super.innerToString() + ", indexSizeInBytes=" + indexSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return IndexWithSize.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * Constructs an {@code IndexWithSize} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @return {@code IndexWithSize}
     */
    @Nonnull
    public static IndexWithSize of(@Nonnull final String tableName,
                                   @Nonnull final String indexName,
                                   final long indexSizeInBytes) {
        return new IndexWithSize(tableName, indexName, indexSizeInBytes);
    }

    /**
     * Constructs an {@code IndexWithSize} object with zero size.
     *
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @return {@code IndexWithSize}
     * @since 0.14.3
     */
    @Nonnull
    public static IndexWithSize of(@Nonnull final String tableName,
                                   @Nonnull final String indexName) {
        return new IndexWithSize(tableName, indexName, 0L);
    }
}
