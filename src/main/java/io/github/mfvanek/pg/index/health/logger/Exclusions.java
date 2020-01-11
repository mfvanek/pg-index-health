/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
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

    private Exclusions(@Nonnull String duplicatedIndexesExclusions,
                       @Nonnull String intersectedIndexesExclusions,
                       @Nonnull String unusedIndexesExclusions,
                       @Nonnull String tablesWithMissingIndexesExclusions,
                       @Nonnull String tablesWithoutPrimaryKeyExclusions,
                       @Nonnull String indexesWithNullValuesExclusions,
                       final long indexSizeThresholdInBytes,
                       final long tableSizeThresholdInBytes) {
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
            Validators.valueNotNegative(thresholdUnitsCount, "thresholdUnitsCount");
            return withIndexSizeThreshold(unit.convertToBytes(thresholdUnitsCount));
        }

        public Builder withTableSizeThreshold(final long tableSizeThresholdInBytes) {
            this.tableSizeThresholdInBytes = Validators.sizeNotNegative(
                    tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
            return this;
        }

        public Builder withTableSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            Validators.valueNotNegative(thresholdUnitsCount, "thresholdUnitsCount");
            return withTableSizeThreshold(unit.convertToBytes(thresholdUnitsCount));
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
                    tableSizeThresholdInBytes);
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
                    '}';
        }
    }
}
