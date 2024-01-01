/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.model.validation.Validators;
import io.github.mfvanek.pg.validation.AdditionalValidators;

import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A listing of exclusions for {@link HealthLogger}.
 *
 * @author Ivan Vakhrushev
 * @see HealthLogger
 * @see AbstractHealthLogger
 */
public class ExclusionsBuilder {

    private static final String THRESHOLD_UNITS_COUNT = "thresholdUnitsCount";

    private String duplicatedIndexesExclusions = "";
    private String intersectedIndexesExclusions = "";
    private String unusedIndexesExclusions = "";
    private String tablesWithMissingIndexesExclusions = "";
    private String tablesWithoutPrimaryKeyExclusions = "";
    private String indexesWithNullValuesExclusions = "";
    private long indexSizeThresholdInBytes;
    private long tableSizeThresholdInBytes;
    private long indexBloatSizeThresholdInBytes;
    private int indexBloatPercentageThreshold;
    private long tableBloatSizeThresholdInBytes;
    private int tableBloatPercentageThreshold;

    ExclusionsBuilder() {
    }

    /**
     * Sets a list of duplicated indexes that should be excluded by {@link HealthLogger}.
     *
     * @param duplicatedIndexesExclusions comma-separated list of duplicated indexes,
     *                                    for example {@code "idx_name_1, idx_name_2"}
     * @return {@code Builder}
     */
    public ExclusionsBuilder withDuplicatedIndexesExclusions(@Nonnull final String duplicatedIndexesExclusions) {
        this.duplicatedIndexesExclusions = Objects.requireNonNull(duplicatedIndexesExclusions);
        return this;
    }

    /**
     * Sets a list of intersected indexes that should be excluded by {@link HealthLogger}.
     *
     * @param intersectedIndexesExclusions comma-separated list of intersected indexes,
     *                                     for example {@code "idx_name_1, idx_name_2"}
     * @return {@code Builder}
     */
    public ExclusionsBuilder withIntersectedIndexesExclusions(@Nonnull final String intersectedIndexesExclusions) {
        this.intersectedIndexesExclusions = Objects.requireNonNull(intersectedIndexesExclusions);
        return this;
    }

    /**
     * Sets a list of unused indexes that should be excluded by {@link HealthLogger}.
     *
     * @param unusedIndexesExclusions comma-separated list of unused indexes,
     *                                for example {@code "idx_name_1, idx_name_2"}
     * @return {@code Builder}
     */
    public ExclusionsBuilder withUnusedIndexesExclusions(@Nonnull final String unusedIndexesExclusions) {
        this.unusedIndexesExclusions = Objects.requireNonNull(unusedIndexesExclusions);
        return this;
    }

    public ExclusionsBuilder withTablesWithMissingIndexesExclusions(
            @Nonnull final String tablesWithMissingIndexesExclusions) {
        this.tablesWithMissingIndexesExclusions = Objects.requireNonNull(tablesWithMissingIndexesExclusions);
        return this;
    }

    public ExclusionsBuilder withTablesWithoutPrimaryKeyExclusions(@Nonnull final String tablesWithoutPrimaryKeyExclusions) {
        this.tablesWithoutPrimaryKeyExclusions = Objects.requireNonNull(tablesWithoutPrimaryKeyExclusions);
        return this;
    }

    public ExclusionsBuilder withIndexesWithNullValuesExclusions(@Nonnull final String indexesWithNullValuesExclusions) {
        this.indexesWithNullValuesExclusions = Objects.requireNonNull(indexesWithNullValuesExclusions);
        return this;
    }

    public ExclusionsBuilder withIndexSizeThreshold(final long indexSizeThresholdInBytes) {
        this.indexSizeThresholdInBytes = Validators.sizeNotNegative(
                indexSizeThresholdInBytes, "indexSizeThresholdInBytes");
        return this;
    }

    public ExclusionsBuilder withIndexSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
        final long indexSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
        return withIndexSizeThreshold(indexSizeInBytes);
    }

    public ExclusionsBuilder withTableSizeThreshold(final long tableSizeThresholdInBytes) {
        this.tableSizeThresholdInBytes = Validators.sizeNotNegative(
                tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
        return this;
    }

    public ExclusionsBuilder withTableSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
        final long tableSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
        return withTableSizeThreshold(tableSizeInBytes);
    }

    public ExclusionsBuilder withIndexBloatSizeThreshold(final long indexBloatSizeThresholdInBytes) {
        this.indexBloatSizeThresholdInBytes = Validators.sizeNotNegative(indexBloatSizeThresholdInBytes,
                "indexBloatSizeThresholdInBytes");
        return this;
    }

    public ExclusionsBuilder withIndexBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
        final long indexBloatSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
        return withIndexBloatSizeThreshold(indexBloatSizeInBytes);
    }

    public ExclusionsBuilder withIndexBloatPercentageThreshold(final int indexBloatPercentageThreshold) {
        this.indexBloatPercentageThreshold = AdditionalValidators.validPercent(
                indexBloatPercentageThreshold, "indexBloatPercentageThreshold");
        return this;
    }

    public ExclusionsBuilder withTableBloatSizeThreshold(final long tableBloatSizeThresholdInBytes) {
        this.tableBloatSizeThresholdInBytes = Validators.sizeNotNegative(
                tableBloatSizeThresholdInBytes, "tableBloatSizeThresholdInBytes");
        return this;
    }

    public ExclusionsBuilder withTableBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
        final long tableBloatSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
        return withTableBloatSizeThreshold(tableBloatSizeInBytes);
    }

    public ExclusionsBuilder withTableBloatPercentageThreshold(final int tableBloatPercentageThreshold) {
        this.tableBloatPercentageThreshold = AdditionalValidators.validPercent(
                tableBloatPercentageThreshold, "tableBloatPercentageThreshold");
        return this;
    }

    public Exclusions build() {
        return new Exclusions(
                duplicatedIndexesExclusions,
                intersectedIndexesExclusions,
                unusedIndexesExclusions,
                tablesWithMissingIndexesExclusions,
                tablesWithoutPrimaryKeyExclusions,
                indexesWithNullValuesExclusions,
                indexSizeThresholdInBytes,
                tableSizeThresholdInBytes,
                indexBloatSizeThresholdInBytes,
                indexBloatPercentageThreshold,
                tableBloatSizeThresholdInBytes,
                tableBloatPercentageThreshold);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return ExclusionsBuilder.class.getSimpleName() + '{' +
                "duplicatedIndexesExclusions='" + duplicatedIndexesExclusions + '\'' +
                ", intersectedIndexesExclusions='" + intersectedIndexesExclusions + '\'' +
                ", unusedIndexesExclusions='" + unusedIndexesExclusions + '\'' +
                ", tablesWithMissingIndexesExclusions='" + tablesWithMissingIndexesExclusions + '\'' +
                ", tablesWithoutPrimaryKeyExclusions='" + tablesWithoutPrimaryKeyExclusions + '\'' +
                ", indexesWithNullValuesExclusions='" + indexesWithNullValuesExclusions + '\'' +
                ", indexSizeThresholdInBytes=" + indexSizeThresholdInBytes +
                ", tableSizeThresholdInBytes=" + tableSizeThresholdInBytes +
                ", indexBloatSizeThresholdInBytes=" + indexBloatSizeThresholdInBytes +
                ", indexBloatPercentageThreshold=" + indexBloatPercentageThreshold +
                ", tableBloatSizeThresholdInBytes=" + tableBloatSizeThresholdInBytes +
                ", tableBloatPercentageThreshold=" + tableBloatPercentageThreshold +
                '}';
    }
}
