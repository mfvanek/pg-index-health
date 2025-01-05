/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.function.Predicate;

/**
 * An abstract base class for filtering {@link DbObject} instances based on a size threshold.
 * This class implements {@link Predicate} and serves as a foundation for classes that
 * filter database objects according to their size in bytes.
 *
 * <p>Subclasses must implement the {@code test} method to specify the filtering logic.</p>
 *
 * @author Ivan Vakhrushev
 * @see DbObject
 * @see Predicate
 * @since 0.13.3
 */
abstract class AbstractFilterBySize implements Predicate<DbObject> {

    /**
     * The size threshold in bytes for filtering {@link DbObject} instances.
     * Subclasses may use this threshold to include or exclude objects based on their size.
     */
    protected final long thresholdInBytes;

    /**
     * Constructs an {@code AbstractFilterBySize} with the specified size threshold.
     *
     * @param thresholdInBytes the minimum size in bytes for an object to pass the filter;
     *                         must be non-negative.
     * @throws IllegalArgumentException if {@code thresholdInBytes} is negative.
     */
    protected AbstractFilterBySize(final long thresholdInBytes) {
        this.thresholdInBytes = Validators.sizeNotNegative(thresholdInBytes, "thresholdInBytes");
    }
}
