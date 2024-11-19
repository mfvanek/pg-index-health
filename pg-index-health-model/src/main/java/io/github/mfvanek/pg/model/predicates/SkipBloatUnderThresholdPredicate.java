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

import io.github.mfvanek.pg.model.bloat.BloatAware;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * A predicate that filters out database objects with bloat values under specified thresholds.
 * Only database objects that implement {@link BloatAware} are evaluated, and they will be skipped if
 * their bloat size or percentage falls below the specified thresholds.
 *
 * @author Ivan Vakhrushev
 * @see Predicate
 * @see BloatAware
 * @since 0.13.3
 */
@Immutable
@ThreadSafe
public final class SkipBloatUnderThresholdPredicate implements Predicate<DbObject> {

    private final long sizeThresholdInBytes;
    private final double percentageThreshold;

    private SkipBloatUnderThresholdPredicate(final long sizeThresholdInBytes, final double percentageThreshold) {
        this.sizeThresholdInBytes = Validators.sizeNotNegative(sizeThresholdInBytes, "sizeThresholdInBytes");
        this.percentageThreshold = Validators.validPercent(percentageThreshold, "percentageThreshold");
    }

    /**
     * Tests whether the specified {@code DbObject} meets or exceeds the bloat thresholds.
     * <p>
     * If {@code dbObject} implements {@link BloatAware}, its bloat size and percentage are checked
     * against the thresholds. If {@code dbObject} does not implement {@link BloatAware}, it passes the test by default.
     * </p>
     *
     * @param dbObject the database object to test
     * @return {@code true} if the {@code dbObject} meets or exceeds the thresholds or does not implement {@link BloatAware}; {@code false} if it is below the thresholds
     */
    @Override
    public boolean test(@Nonnull final DbObject dbObject) {
        if (sizeThresholdInBytes == 0L && percentageThreshold == 0.0) {
            return true;
        }
        if (!(dbObject instanceof BloatAware)) {
            return true;
        }
        final BloatAware bloatAware = (BloatAware) dbObject;
        return bloatAware.getBloatSizeInBytes() >= sizeThresholdInBytes &&
            bloatAware.getBloatPercentage() >= percentageThreshold;
    }

    /**
     * Creates a predicate to skip {@link BloatAware} objects with bloat below specified thresholds.
     *
     * @param sizeThresholdInBytes minimum bloat size in bytes required for a {@link BloatAware} object to pass the test; must be non-negative
     * @param percentageThreshold  minimum bloat percentage required for a {@link BloatAware} object to pass the test; must be a valid percentage (0-100)
     * @return a {@link Predicate} that skips objects below the specified bloat thresholds
     */
    @Nonnull
    public static Predicate<DbObject> of(final long sizeThresholdInBytes, final double percentageThreshold) {
        return new SkipBloatUnderThresholdPredicate(sizeThresholdInBytes, percentageThreshold);
    }
}
