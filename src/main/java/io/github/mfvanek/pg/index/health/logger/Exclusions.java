/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.health.logger;

import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.utils.Validators;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A listing of exclusions for {@link IndexesHealthLogger}.
 *
 * @author Ivan Vakhrushev
 * @see IndexesHealthLogger
 * @see AbstractIndexesHealthLogger
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

    private Exclusions(@Nonnull String duplicatedIndexesExclusions,
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
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String EMPTY = "";

        private String duplicatedIndexesExclusions = EMPTY;
        private String intersectedIndexesExclusions = EMPTY;
        private String unusedIndexesExclusions = EMPTY;
        private String tablesWithMissingIndexesExclusions = EMPTY;
        private String tablesWithoutPrimaryKeyExclusions = EMPTY;
        private String indexesWithNullValuesExclusions = EMPTY;
        private long indexSizeThresholdInBytes = 0L;
        private long tableSizeThresholdInBytes = 0L;
        private long indexBloatSizeThresholdInBytes = 0L;
        private int indexBloatPercentageThreshold = 0;
        private long tableBloatSizeThresholdInBytes = 0L;
        private int tableBloatPercentageThreshold = 0;

        private Builder() {
        }

        /**
         * Sets a list of duplicated indexes that should be excluded by {@link IndexesHealthLogger}.
         *
         * @param duplicatedIndexesExclusions comma-separated list of duplicated indexes,
         *                                    for example {@code "idx_name_1, idx_name_2"}
         * @return {@code Builder}
         */
        public Builder withDuplicatedIndexesExclusions(@Nonnull final String duplicatedIndexesExclusions) {
            this.duplicatedIndexesExclusions = Objects.requireNonNull(duplicatedIndexesExclusions);
            return this;
        }

        /**
         * Sets a list of intersected indexes that should be excluded by {@link IndexesHealthLogger}.
         *
         * @param intersectedIndexesExclusions comma-separated list of intersected indexes,
         *                                     for example {@code "idx_name_1, idx_name_2"}
         * @return {@code Builder}
         */
        public Builder withIntersectedIndexesExclusions(@Nonnull final String intersectedIndexesExclusions) {
            this.intersectedIndexesExclusions = Objects.requireNonNull(intersectedIndexesExclusions);
            return this;
        }

        /**
         * Sets a list of unused indexes that should be excluded by {@link IndexesHealthLogger}.
         *
         * @param unusedIndexesExclusions comma-separated list of unused indexes,
         *                                for example {@code "idx_name_1, idx_name_2"}
         * @return {@code Builder}
         */
        public Builder withUnusedIndexesExclusions(@Nonnull final String unusedIndexesExclusions) {
            this.unusedIndexesExclusions = Objects.requireNonNull(unusedIndexesExclusions);
            return this;
        }

        public Builder withTablesWithMissingIndexesExclusions(
                @Nonnull final String tablesWithMissingIndexesExclusions) {
            this.tablesWithMissingIndexesExclusions = Objects.requireNonNull(tablesWithMissingIndexesExclusions);
            return this;
        }

        public Builder withTablesWithoutPrimaryKeyExclusions(@Nonnull final String tablesWithoutPrimaryKeyExclusions) {
            this.tablesWithoutPrimaryKeyExclusions = Objects.requireNonNull(tablesWithoutPrimaryKeyExclusions);
            return this;
        }

        public Builder withIndexesWithNullValuesExclusions(@Nonnull final String indexesWithNullValuesExclusions) {
            this.indexesWithNullValuesExclusions = Objects.requireNonNull(indexesWithNullValuesExclusions);
            return this;
        }

        public Builder withIndexSizeThreshold(final long indexSizeThresholdInBytes) {
            this.indexSizeThresholdInBytes = Validators.sizeNotNegative(
                    indexSizeThresholdInBytes, "indexSizeThresholdInBytes");
            return this;
        }

        public Builder withIndexSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexSizeInBytes = unit.convertToBytes(
                    Validators.argumentNotNegative(thresholdUnitsCount, "thresholdUnitsCount"));
            return withIndexSizeThreshold(indexSizeInBytes);
        }

        public Builder withTableSizeThreshold(final long tableSizeThresholdInBytes) {
            this.tableSizeThresholdInBytes = Validators.sizeNotNegative(
                    tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
            return this;
        }

        public Builder withTableSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long tableSizeInBytes = unit.convertToBytes(
                    Validators.argumentNotNegative(thresholdUnitsCount, "thresholdUnitsCount"));
            return withTableSizeThreshold(tableSizeInBytes);
        }

        public Builder withIndexBloatSizeThreshold(final long indexBloatSizeThresholdInBytes) {
            this.indexBloatSizeThresholdInBytes = Validators.sizeNotNegative(indexBloatSizeThresholdInBytes,
                    "indexBloatSizeThresholdInBytes");
            return this;
        }

        public Builder withIndexBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexBloatSizeInBytes = unit.convertToBytes(
                    Validators.argumentNotNegative(thresholdUnitsCount, "thresholdUnitsCount"));
            return withIndexBloatSizeThreshold(indexBloatSizeInBytes);
        }

        public Builder withIndexBloatPercentageThreshold(final int indexBloatPercentageThreshold) {
            this.indexBloatPercentageThreshold = Validators.validPercent(
                    indexBloatPercentageThreshold, "indexBloatPercentageThreshold");
            return this;
        }

        public Builder withTableBloatSizeThreshold(final long tableBloatSizeThresholdInBytes) {
            this.tableBloatSizeThresholdInBytes = Validators.sizeNotNegative(
                    tableBloatSizeThresholdInBytes, "tableBloatSizeThresholdInBytes");
            return this;
        }

        public Builder withTableBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long tableBloatSizeInBytes = unit.convertToBytes(
                    Validators.argumentNotNegative(thresholdUnitsCount, "thresholdUnitsCount"));
            return withTableBloatSizeThreshold(tableBloatSizeInBytes);
        }

        public Builder withTableBloatPercentageThreshold(final int tableBloatPercentageThreshold) {
            this.tableBloatPercentageThreshold = Validators.validPercent(
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

        @Override
        public String toString() {
            return Builder.class.getSimpleName() + '{' +
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
}
