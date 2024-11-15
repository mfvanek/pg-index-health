/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.index.IndexSizeAware;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A predicate that filters out small indexes based on a specified size threshold.
 * This class extends {@link AbstractFilterBySize} and evaluates {@link IndexSizeAware}
 * instances to determine if they meet or exceed the specified minimum index size.
 *
 * @author Ivan Vakhrushev
 * @see IndexSizeAware
 * @see DbObject
 * @see AbstractFilterBySize
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipSmallIndexesPredicate extends AbstractFilterBySize {

    private SkipSmallIndexesPredicate(final long thresholdInBytes) {
        super(thresholdInBytes);
    }

    /**
     * Evaluates whether the given {@link DbObject} should pass the filter based on its size.
     * If the object is not an instance of {@link IndexSizeAware}, or if the threshold is zero,
     * it automatically passes the filter. Otherwise, the object's size is compared to the threshold.
     *
     * @param dbObject the {@code DbObject} to be evaluated; must not be null.
     * @return {@code true} if the table size is greater than or equal to the threshold, or if the object is not {@code IndexSizeAware}; {@code false} otherwise.
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (thresholdInBytes == 0L) {
            return true;
        }
        if (!(dbObject instanceof IndexSizeAware)) {
            return true;
        }
        final IndexSizeAware indexSizeAware = (IndexSizeAware) dbObject;
        return indexSizeAware.getIndexSizeInBytes() >= thresholdInBytes;
    }

    /**
     * Creates a {@code SkipSmallIndexesPredicate} with the specified size threshold.
     *
     * @param thresholdInBytes the minimum index size in bytes required for an index to pass the filter; must be non-negative.
     * @return a predicate that filters out indexes smaller than the specified size threshold.
     */
    @Nonnull
    public static Predicate<DbObject> of(final long thresholdInBytes) {
        return new SkipSmallIndexesPredicate(thresholdInBytes);
    }
}
