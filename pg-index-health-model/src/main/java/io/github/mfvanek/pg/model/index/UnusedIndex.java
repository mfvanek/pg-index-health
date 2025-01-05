/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents an unused database index with information about its usage and size.
 * <p>
 * This class extends {@link IndexWithSize} to include additional details on the
 * number of scans (or accesses) the index has had, providing insight into whether
 * the index is actively used or potentially redundant.
 * </p>
 */
@Immutable
public final class UnusedIndex extends IndexWithSize {

    /**
     * The number of scans performed on this index.
     */
    private final long indexScans;

    private UnusedIndex(@Nonnull final String tableName,
                        @Nonnull final String indexName,
                        final long indexSizeInBytes,
                        final long indexScans) {
        super(tableName, indexName, indexSizeInBytes);
        this.indexScans = Validators.countNotNegative(indexScans, "indexScans");
    }

    /**
     * Returns the number of scans performed on this index.
     *
     * @return the scan count, never negative
     */
    public long getIndexScans() {
        return indexScans;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return UnusedIndex.class.getSimpleName() + '{' +
            innerToString() +
            ", indexScans=" + indexScans +
            '}';
    }

    /**
     * Creates a new {@code UnusedIndex} instance with the specified parameters.
     *
     * @param tableName        the name of the table associated with the index, must be non-null
     * @param indexName        the name of the index, must be non-null
     * @param indexSizeInBytes the size of the index in bytes
     * @param indexScans       the number of times the index has been scanned, must be non-negative
     * @return a new {@code UnusedIndex} instance
     * @throws NullPointerException     if {@code tableName} or {@code indexName} is null
     * @throws IllegalArgumentException if {@code indexScans} is negative
     */
    @Nonnull
    public static UnusedIndex of(@Nonnull final String tableName,
                                 @Nonnull final String indexName,
                                 final long indexSizeInBytes,
                                 final long indexScans) {
        return new UnusedIndex(tableName, indexName, indexSizeInBytes, indexScans);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with the specified parameters and given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        the name of the table associated with the index, must be non-null
     * @param indexName        the name of the index, must be non-null
     * @param indexSizeInBytes the size of the index in bytes
     * @param indexScans       the number of times the index has been scanned, must be non-negative
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    @Nonnull
    public static UnusedIndex of(@Nonnull final PgContext pgContext,
                                 @Nonnull final String tableName,
                                 @Nonnull final String indexName,
                                 final long indexSizeInBytes,
                                 final long indexScans) {
        return of(PgContext.enrichWith(tableName, pgContext), PgContext.enrichWith(indexName, pgContext), indexSizeInBytes, indexScans);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with zero size.
     *
     * @param tableName the name of the table associated with the index, must be non-null
     * @param indexName the name of the index, must be non-null
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    @Nonnull
    public static UnusedIndex of(@Nonnull final String tableName,
                                 @Nonnull final String indexName) {
        return of(tableName, indexName, 0L, 0L);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with zero size and given context.
     *
     * @param pgContext the schema context to enrich table and index name; must be non-null.
     * @param tableName the name of the table associated with the index, must be non-null
     * @param indexName the name of the index, must be non-null
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    @Nonnull
    public static UnusedIndex of(@Nonnull final PgContext pgContext,
                                 @Nonnull final String tableName,
                                 @Nonnull final String indexName) {
        return of(pgContext, tableName, indexName, 0L, 0L);
    }
}
