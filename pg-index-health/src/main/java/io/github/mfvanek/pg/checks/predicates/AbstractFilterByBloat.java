/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.BloatAware;
import io.github.mfvanek.pg.model.validation.Validators;
import io.github.mfvanek.pg.validation.AdditionalValidators;

import javax.annotation.Nonnull;

/**
 * Base class for filters by bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
abstract class AbstractFilterByBloat {

    private final long sizeThresholdInBytes;
    private final int percentageThreshold;

    protected AbstractFilterByBloat(final long sizeThresholdInBytes, final int percentageThreshold) {
        this.sizeThresholdInBytes = Validators.sizeNotNegative(sizeThresholdInBytes, "sizeThresholdInBytes");
        this.percentageThreshold = AdditionalValidators.validPercent(percentageThreshold, "percentageThreshold");
    }

    protected boolean isOk(@Nonnull final BloatAware bloatAware) {
        return bloatAware.getBloatSizeInBytes() >= sizeThresholdInBytes &&
            bloatAware.getBloatPercentage() >= percentageThreshold;
    }
}
