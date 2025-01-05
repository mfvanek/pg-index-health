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

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.validation.Validators;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public final class IndexWithBloat extends IndexWithSize implements BloatAware {

    private final long bloatSizeInBytes;
    private final double bloatPercentage;

    private IndexWithBloat(@Nonnull final String tableName,
                           @Nonnull final String indexName,
                           final long indexSizeInBytes,
                           final long bloatSizeInBytes,
                           final double bloatPercentage) {
        super(tableName, indexName, indexSizeInBytes);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.validPercent(bloatPercentage, "bloatPercentage");
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
    @Nonnull
    @Override
    protected String innerToString() {
        return super.innerToString() + ", bloatSizeInBytes=" + bloatSizeInBytes +
            ", bloatPercentage=" + bloatPercentage;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return IndexWithBloat.class.getSimpleName() + '{' + innerToString() + '}';
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
    @Nonnull
    public static IndexWithBloat of(@Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return new IndexWithBloat(tableName, indexName, indexSizeInBytes, bloatSizeInBytes, bloatPercentage);
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
    @Nonnull
    public static IndexWithBloat of(@Nonnull final PgContext pgContext,
                                    @Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    final long bloatSizeInBytes,
                                    final double bloatPercentage) {
        return of(PgContext.enrichWith(tableName, pgContext), PgContext.enrichWith(indexName, pgContext),
            indexSizeInBytes, bloatSizeInBytes, bloatPercentage);
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
    @Nonnull
    public static IndexWithBloat of(@Nonnull final PgContext pgContext,
                                    @Nonnull final String tableName,
                                    @Nonnull final String indexName) {
        return of(pgContext, tableName, indexName, 0L, 0L, 0.0);
    }
}
