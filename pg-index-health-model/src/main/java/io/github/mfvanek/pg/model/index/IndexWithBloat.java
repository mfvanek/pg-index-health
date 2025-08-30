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

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a database index with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
public final class IndexWithBloat extends AbstractIndexAware implements BloatAware, Comparable<IndexWithBloat> {

    private final long bloatSizeInBytes;
    private final double bloatPercentage;

    private IndexWithBloat(final Index index,
                           final long bloatSizeInBytes,
                           final double bloatPercentage) {
        super(index);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, BLOAT_SIZE_IN_BYTES_FIELD);
        this.bloatPercentage = Validators.validPercent(bloatPercentage, BLOAT_PERCENTAGE_FIELD);
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
    public double getBloatPercentage() {
        return bloatPercentage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return IndexWithBloat.class.getSimpleName() + '{' +
            index.innerToString() +
            ", " + BLOAT_SIZE_IN_BYTES_FIELD + '=' + bloatSizeInBytes +
            ", " + BLOAT_PERCENTAGE_FIELD + '=' + bloatPercentage +
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

        if (!(other instanceof final IndexWithBloat that)) {
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
    public int compareTo(final IndexWithBloat other) {
        Objects.requireNonNull(other, "other cannot be null");
        return index.compareTo(other.index);
    }

    /**
     * Constructs a {@code IndexWithBloat} object.
     *
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code IndexWithBloat}
     */
    public static IndexWithBloat of(final String tableName,
                                    final String indexName,
                                    final long indexSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return of(Index.of(tableName, indexName, indexSizeInBytes), bloatSizeInBytes, bloatPercentage);
    }

    /**
     * Constructs a {@code IndexWithBloat} object with given context.
     *
     * @param pgContext        the schema context to enrich table and index name; must be non-null.
     * @param tableName        table name; should be non-blank.
     * @param indexName        index name; should be non-blank.
     * @param indexSizeInBytes index size in bytes; should be positive or zero.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code IndexWithBloat}
     * @since 0.14.3
     */
    public static IndexWithBloat of(final PgContext pgContext,
                                    final String tableName,
                                    final String indexName,
                                    final long indexSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return of(Index.of(pgContext, tableName, indexName, indexSizeInBytes), bloatSizeInBytes, bloatPercentage);
    }

    /**
     * Constructs a {@code IndexWithBloat} object with given context and zero bloat.
     *
     * @param pgContext the schema context to enrich table and index name; must be non-null.
     * @param tableName table name; should be non-blank.
     * @param indexName index name; should be non-blank.
     * @return {@code IndexWithBloat}
     * @since 0.14.3
     */
    public static IndexWithBloat of(final PgContext pgContext,
                                    final String tableName,
                                    final String indexName) {
        return of(pgContext, tableName, indexName, 0L, 0L, 0.0);
    }

    /**
     * Constructs a {@code IndexWithBloat} object with given index and bloat.
     *
     * @param index            index; must be non-null.
     * @param bloatSizeInBytes bloat amount in bytes; should be positive or zero.
     * @param bloatPercentage  bloat percentage in the range from 0 to 100 inclusive.
     * @return {@code IndexWithBloat}
     * @since 0.15.0
     */
    public static IndexWithBloat of(final Index index,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return new IndexWithBloat(index, bloatSizeInBytes, bloatPercentage);
    }
}
