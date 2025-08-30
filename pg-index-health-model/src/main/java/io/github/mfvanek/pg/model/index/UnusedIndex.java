/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of an unused database index with information about its usage and size.
 * <p>
 * This class extends {@link Index} to include additional details on the
 * number of scans (or accesses) the index has had, providing insight into whether
 * the index is actively used or potentially redundant.
 * </p>
 */
@SuppressWarnings("checkstyle:EqualsHashCode")
public final class UnusedIndex extends AbstractIndexAware implements Comparable<UnusedIndex> {

    /**
     * Represents the field name for the number of scans performed on an index.
     */
    public static final String INDEX_SCANS_FIELD = "indexScans";

    /**
     * The number of scans performed on this index.
     */
    private final long indexScans;

    private UnusedIndex(final Index index,
                        final long indexScans) {
        super(index);
        this.indexScans = Validators.countNotNegative(indexScans, INDEX_SCANS_FIELD);
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
    @Override
    public String toString() {
        return UnusedIndex.class.getSimpleName() + '{' +
            index.innerToString() +
            ", " + INDEX_SCANS_FIELD + '=' + indexScans +
            '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final UnusedIndex that)) {
            return false;
        }

        return Objects.equals(index, that.index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final UnusedIndex other) {
        Objects.requireNonNull(other, "other cannot be null");
        return index.compareTo(other.index);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with the specified parameters.
     *
     * @param tableName        the name of the table associated with the index; must be non-null
     * @param indexName        the name of the index; must be non-null
     * @param indexSizeInBytes the size of the index in bytes
     * @param indexScans       the number of times the index has been scanned; must be non-negative
     * @return a new {@code UnusedIndex} instance
     * @throws NullPointerException     if {@code tableName} or {@code indexName} is null
     * @throws IllegalArgumentException if {@code indexScans} is negative
     */
    public static UnusedIndex of(final String tableName,
                                 final String indexName,
                                 final long indexSizeInBytes,
                                 final long indexScans) {
        return of(Index.of(tableName, indexName, indexSizeInBytes), indexScans);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with the specified parameters and given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        the name of the table associated with the index; must be non-null
     * @param indexName        the name of the index; must be non-null
     * @param indexSizeInBytes the size of the index in bytes
     * @param indexScans       the number of times the index has been scanned; must be non-negative
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    public static UnusedIndex of(final PgContext pgContext,
                                 final String tableName,
                                 final String indexName,
                                 final long indexSizeInBytes,
                                 final long indexScans) {
        return of(Index.of(pgContext, tableName, indexName, indexSizeInBytes), indexScans);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with zero size.
     *
     * @param tableName the name of the table associated with the index; must be non-null
     * @param indexName the name of the index; must be non-null
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    public static UnusedIndex of(final String tableName,
                                 final String indexName) {
        return of(tableName, indexName, 0L, 0L);
    }

    /**
     * Creates a new {@code UnusedIndex} instance with zero size and given context.
     *
     * @param pgContext the schema context to enrich table and index name; must be non-null.
     * @param tableName the name of the table associated with the index; must be non-null
     * @param indexName the name of the index; must be non-null
     * @return a new {@code UnusedIndex} instance
     * @since 0.14.3
     */
    public static UnusedIndex of(final PgContext pgContext,
                                 final String tableName,
                                 final String indexName) {
        return of(pgContext, tableName, indexName, 0L, 0L);
    }

    /**
     * Constructs a {@code UnusedIndex} object.
     *
     * @param index      index; should be non-null.
     * @param indexScans the number of times the index has been scanned; must be non-negative
     * @return a new {@code UnusedIndex} instance
     * @since 0.15.0
     */
    public static UnusedIndex of(final Index index,
                                 final long indexScans) {
        return new UnusedIndex(index, indexScans);
    }
}
