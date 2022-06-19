/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance.predicates;

import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.utils.Validators;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter indexes by their size.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.1
 */
public class FilterIndexesBySizePredicate implements Predicate<IndexSizeAware> {

    final long threshold;

    public FilterIndexesBySizePredicate(final long threshold) {
        this.threshold = Validators.sizeNotNegative(threshold, "threshold");
    }

    @Override
    public boolean test(@Nonnull final IndexSizeAware indexSizeAware) {
        if (threshold == 0) {
            return true;
        }
        return indexSizeAware.getIndexSizeInBytes() >= threshold;
    }
}
