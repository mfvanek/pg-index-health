/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.index.IndexBloatAware;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter indexes by their bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.1
 */
public class FilterIndexesByBloatPredicate extends AbstractFilterByBloat implements Predicate<IndexBloatAware> {

    private FilterIndexesByBloatPredicate(final long sizeThresholdInBytes, final int percentageThreshold) {
        super(sizeThresholdInBytes, percentageThreshold);
    }

    @Override
    public boolean test(@Nonnull final IndexBloatAware indexBloatAware) {
        return isOk(indexBloatAware);
    }

    @Nonnull
    public static Predicate<IndexBloatAware> of(final long sizeThresholdInBytes, final int percentageThreshold) {
        return new FilterIndexesByBloatPredicate(sizeThresholdInBytes, percentageThreshold);
    }
}
