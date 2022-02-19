/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.utils.Validators;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
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

    Exclusions(@Nonnull String duplicatedIndexesExclusions,
               @Nonnull String intersectedIndexesExclusions,
               @Nonnull String unusedIndexesExclusions,
               @Nonnull String tablesWithMissingIndexesExclusions,
               @Nonnull String tablesWithoutPrimaryKeyExclusions,
               @Nonnull String indexesWithNullValuesExclusions,
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
        this.indexBloatPercentageThreshold = Validators.validPercent(
                indexBloatPercentageThreshold, "indexBloatPercentageThreshold");
        this.tableBloatSizeThresholdInBytes = Validators.sizeNotNegative(
                tableBloatSizeThresholdInBytes, "tableBloatSizeThresholdInBytes");
        this.tableBloatPercentageThreshold = Validators.validPercent(
                tableBloatPercentageThreshold, "tableBloatPercentageThreshold");
    }

    private static Set<String> prepareExclusions(@Nonnull final String rawExclusions) {
        Objects.requireNonNull(rawExclusions);
        final Set<String> exclusions = new HashSet<>();
        if (StringUtils.isNotBlank(rawExclusions)) {
            final String[] tables = rawExclusions.toLowerCase().split(",");
            for (String tableName : tables) {
                if (StringUtils.isNotBlank(tableName)) {
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
