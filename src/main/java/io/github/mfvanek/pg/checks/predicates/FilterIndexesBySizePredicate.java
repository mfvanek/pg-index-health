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

import io.github.mfvanek.pg.model.index.IndexSizeAware;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter indexes by their size.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class FilterIndexesBySizePredicate extends AbstractFilterBySize implements Predicate<IndexSizeAware> {

    private FilterIndexesBySizePredicate(final long thresholdInBytes) {
        super(thresholdInBytes);
    }

    @Override
    public boolean test(@Nonnull final IndexSizeAware indexSizeAware) {
        return indexSizeAware.getIndexSizeInBytes() >= thresholdInBytes;
    }

    @Nonnull
    public static Predicate<IndexSizeAware> of(final long thresholdInBytes) {
        return new FilterIndexesBySizePredicate(thresholdInBytes);
    }
}
