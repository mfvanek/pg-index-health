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

import io.github.mfvanek.pg.model.validation.Validators;
import io.github.mfvanek.pg.validation.AdditionalValidators;

import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * A listing of exclusions for {@link HealthLogger}.
 *
 * @author Ivan Vakhrushev
 * @see HealthLogger
 * @see AbstractHealthLogger
 */
public class Exclusions {

    private final Set<String> duplicatedIndexesExclusions;
    private final Set<String> intersectedIndexesExclusions;
    private final Set<String> unusedIndexesExclusions;
    private final Set<String> tablesWithMissingIndexesExclusions;
    private final Set<String> tablesWithoutPrimaryKeyExclusions;
    private final Set<String> indexesWithNullValuesExclusions;
    private final long indexSizeThresholdInBytes;
    private final long tableSizeThresholdInBytes;
    private final long indexBloatSizeThresholdInBytes;
    private final int indexBloatPercentageThreshold;
    private final long tableBloatSizeThresholdInBytes;
    private final int tableBloatPercentageThreshold;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    Exclusions(@Nonnull final String duplicatedIndexesExclusions,
               @Nonnull final String intersectedIndexesExclusions,
               @Nonnull final String unusedIndexesExclusions,
               @Nonnull final String tablesWithMissingIndexesExclusions,
               @Nonnull final String tablesWithoutPrimaryKeyExclusions,
               @Nonnull final String indexesWithNullValuesExclusions,
               final long indexSizeThresholdInBytes,
               final long tableSizeThresholdInBytes,
               final long indexBloatSizeThresholdInBytes,
               final int indexBloatPercentageThreshold,
               final long tableBloatSizeThresholdInBytes,
               final int tableBloatPercentageThreshold) {
        this.duplicatedIndexesExclusions = prepareExclusions(duplicatedIndexesExclusions);
        this.intersectedIndexesExclusions = prepareExclusions(intersectedIndexesExclusions);
        this.unusedIndexesExclusions = prepareExclusions(unusedIndexesExclusions);
        this.tablesWithMissingIndexesExclusions = prepareExclusions(tablesWithMissingIndexesExclusions);
        this.tablesWithoutPrimaryKeyExclusions = prepareExclusions(tablesWithoutPrimaryKeyExclusions);
        this.indexesWithNullValuesExclusions = prepareExclusions(indexesWithNullValuesExclusions);
        this.indexSizeThresholdInBytes = Validators.sizeNotNegative(
                indexSizeThresholdInBytes, "indexSizeThresholdInBytes");
        this.tableSizeThresholdInBytes = Validators.sizeNotNegative(
                tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
        this.indexBloatSizeThresholdInBytes = Validators.sizeNotNegative(
                indexBloatSizeThresholdInBytes, "indexBloatSizeThresholdInBytes");
        this.indexBloatPercentageThreshold = AdditionalValidators.validPercent(
                indexBloatPercentageThreshold, "indexBloatPercentageThreshold");
        this.tableBloatSizeThresholdInBytes = Validators.sizeNotNegative(
                tableBloatSizeThresholdInBytes, "tableBloatSizeThresholdInBytes");
        this.tableBloatPercentageThreshold = AdditionalValidators.validPercent(
                tableBloatPercentageThreshold, "tableBloatPercentageThreshold");
    }

    private static Set<String> prepareExclusions(@Nonnull final String rawExclusions) {
        Objects.requireNonNull(rawExclusions, "rawExclusions cannot be null");
        final Set<String> exclusions = new HashSet<>();
        if (!rawExclusions.isBlank()) {
            final String[] tables = rawExclusions.toLowerCase(Locale.ROOT).split(",");
            for (final String tableName : tables) {
                if (!tableName.isBlank()) {
                    exclusions.add(tableName.trim());
                }
            }
        }
        return exclusions;
    }

    @Nonnull
    Set<String> getDuplicatedIndexesExclusions() {
        return duplicatedIndexesExclusions;
    }

    @Nonnull
    Set<String> getIntersectedIndexesExclusions() {
        return intersectedIndexesExclusions;
    }

    @Nonnull
    Set<String> getUnusedIndexesExclusions() {
        return unusedIndexesExclusions;
    }

    @Nonnull
    Set<String> getTablesWithMissingIndexesExclusions() {
        return tablesWithMissingIndexesExclusions;
    }

    @Nonnull
    Set<String> getTablesWithoutPrimaryKeyExclusions() {
        return tablesWithoutPrimaryKeyExclusions;
    }

    @Nonnull
    Set<String> getIndexesWithNullValuesExclusions() {
        return indexesWithNullValuesExclusions;
    }

    long getIndexSizeThresholdInBytes() {
        return indexSizeThresholdInBytes;
    }

    long getTableSizeThresholdInBytes() {
        return tableSizeThresholdInBytes;
    }

    long getIndexBloatSizeThresholdInBytes() {
        return indexBloatSizeThresholdInBytes;
    }

    int getIndexBloatPercentageThreshold() {
        return indexBloatPercentageThreshold;
    }

    public long getTableBloatSizeThresholdInBytes() {
        return tableBloatSizeThresholdInBytes;
    }

    public int getTableBloatPercentageThreshold() {
        return tableBloatPercentageThreshold;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return Exclusions.class.getSimpleName() + '{' +
                "duplicatedIndexesExclusions=" + duplicatedIndexesExclusions +
                ", intersectedIndexesExclusions=" + intersectedIndexesExclusions +
                ", unusedIndexesExclusions=" + unusedIndexesExclusions +
                ", tablesWithMissingIndexesExclusions=" + tablesWithMissingIndexesExclusions +
                ", tablesWithoutPrimaryKeyExclusions=" + tablesWithoutPrimaryKeyExclusions +
                ", indexesWithNullValuesExclusions=" + indexesWithNullValuesExclusions +
                ", indexSizeThresholdInBytes=" + indexSizeThresholdInBytes +
                ", tableSizeThresholdInBytes=" + tableSizeThresholdInBytes +
                ", indexBloatSizeThresholdInBytes=" + indexBloatSizeThresholdInBytes +
                ", indexBloatPercentageThreshold=" + indexBloatPercentageThreshold +
                ", tableBloatSizeThresholdInBytes=" + tableBloatSizeThresholdInBytes +
                ", tableBloatPercentageThreshold=" + tableBloatPercentageThreshold +
                '}';
    }

    /**
     * Returns empty exclusions list.
     *
     * @return empty {@code Exclusions} object
     */
    public static Exclusions empty() {
        return builder().build();
    }

    /**
     * Returns a {@code Builder} for constructing {@link Exclusions} object.
     *
     * @return {@code Builder}
     */
    public static ExclusionsBuilder builder() {
        return new ExclusionsBuilder();
    }
}
