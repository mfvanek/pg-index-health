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

import io.github.mfvanek.pg.model.validation.Validators;

/**
 * Base class for filters by size.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
abstract class AbstractFilterBySize {

    protected final long thresholdInBytes;

    protected AbstractFilterBySize(final long thresholdInBytes) {
        this.thresholdInBytes = Validators.sizeNotNegative(thresholdInBytes, "thresholdInBytes");
    }
}
