/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.table.TableBloatAware;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter tables by their bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class FilterTablesByBloatPredicate extends AbstractFilterByBloat implements Predicate<TableBloatAware> {

    private FilterTablesByBloatPredicate(final long sizeThresholdInBytes, final int percentageThreshold) {
        super(sizeThresholdInBytes, percentageThreshold);
    }

    @Override
    public boolean test(@Nonnull final TableBloatAware tableBloatAware) {
        return isOk(tableBloatAware);
    }

    @Nonnull
    public static Predicate<TableBloatAware> of(final long sizeThresholdInBytes, final int percentageThreshold) {
        return new FilterTablesByBloatPredicate(sizeThresholdInBytes, percentageThreshold);
    }
}
