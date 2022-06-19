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

import io.github.mfvanek.pg.model.table.TableBloatAware;
import io.github.mfvanek.pg.utils.Validators;

import java.util.function.Predicate;
import javax.annotation.Nonnull;

/**
 * Allows filter tables by their bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.1
 */
public class FilterTablesByBloatPredicate implements Predicate<TableBloatAware> {

    private final long sizeThreshold;
    private final int percentageThreshold;

    public FilterTablesByBloatPredicate(final long sizeThreshold, final int percentageThreshold) {
        this.sizeThreshold = Validators.sizeNotNegative(sizeThreshold, "sizeThreshold");
        this.percentageThreshold = Validators.validPercent(percentageThreshold, "percentageThreshold");
    }

    @Override
    public boolean test(@Nonnull final TableBloatAware tableBloatAware) {
        if (sizeThreshold == 0L && percentageThreshold == 0) {
            return true;
        }
        return tableBloatAware.getBloatSizeInBytes() >= sizeThreshold &&
                tableBloatAware.getBloatPercentage() >= percentageThreshold;
    }
}
