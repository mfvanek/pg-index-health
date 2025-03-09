/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.model.units.MemoryUnit;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * A listing of exclusions for {@link HealthLogger}.
 * <p>
 * Represents configuration for exclusions, such as index, table, or sequence names
 * that should be excluded from processing, along with size and bloat thresholds.
 * This class uses a builder pattern for object construction.
 *
 * @author Ivan Vakhrushev
 * @see HealthLogger
 * @see AbstractHealthLogger
 */
public final class Exclusions {

    /**
     * A set of index names to exclude.
     */
    private final Set<String> indexNameExclusions;

    /**
     * A set of table names to exclude.
     */
    private final Set<String> tableNameExclusions;

    /**
     * A set of sequence names to exclude.
     */
    private final Set<String> sequenceNameExclusions;

    /**
     * Threshold for the size of indexes in bytes.
     */
    private long indexSizeThresholdInBytes;

    /**
     * Threshold for the size of tables in bytes.
     */
    private long tableSizeThresholdInBytes;

    /**
     * Threshold for the bloat size in bytes.
     */
    private long bloatSizeThresholdInBytes;

    /**
     * Threshold for the bloat percentage.
     */
    private double bloatPercentageThreshold;

    private Exclusions() {
        this.indexNameExclusions = new HashSet<>();
        this.tableNameExclusions = new HashSet<>();
        this.sequenceNameExclusions = new HashSet<>();
    }

    /**
     * Gets the bloat percentage threshold.
     *
     * @return the bloat percentage threshold
     */
    public double getBloatPercentageThreshold() {
        return bloatPercentageThreshold;
    }

    /**
     * Gets the bloat size threshold in bytes.
     *
     * @return the bloat size threshold in bytes
     */
    public long getBloatSizeThresholdInBytes() {
        return bloatSizeThresholdInBytes;
    }

    /**
     * Gets the table size threshold in bytes.
     *
     * @return the table size threshold in bytes
     */
    public long getTableSizeThresholdInBytes() {
        return tableSizeThresholdInBytes;
    }

    /**
     * Gets the index size threshold in bytes.
     *
     * @return the index size threshold in bytes
     */
    public long getIndexSizeThresholdInBytes() {
        return indexSizeThresholdInBytes;
    }

    /**
     * Gets the set of table names to exclude.
     *
     * @return a collection of table names to exclude
     */
    @Nonnull
    public Collection<String> getTableNameExclusions() {
        return Set.copyOf(tableNameExclusions);
    }

    /**
     * Gets the set of index names to exclude.
     *
     * @return a collection of index names to exclude
     */
    @Nonnull
    public Collection<String> getIndexNameExclusions() {
        return Set.copyOf(indexNameExclusions);
    }

    /**
     * Gets the set of sequence names to exclude.
     *
     * @return a collection of sequence names to exclude
     */
    @Nonnull
    public Collection<String> getSequenceNameExclusions() {
        return Set.copyOf(sequenceNameExclusions);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Exclusions.class.getSimpleName() + '{' +
            "indexNameExclusions=" + indexNameExclusions +
            ", tableNameExclusions=" + tableNameExclusions +
            ", sequenceNameExclusions=" + sequenceNameExclusions +
            ", indexSizeThresholdInBytes=" + indexSizeThresholdInBytes +
            ", tableSizeThresholdInBytes=" + tableSizeThresholdInBytes +
            ", bloatSizeThresholdInBytes=" + bloatSizeThresholdInBytes +
            ", bloatPercentageThreshold=" + bloatPercentageThreshold +
            '}';
    }

    /**
     * Returns an empty {@code Exclusions} instance.
     *
     * @return an empty {@code Exclusions} object
     */
    @Nonnull
    public static Exclusions empty() {
        return builder().build();
    }

    /**
     * Returns a builder for constructing {@link Exclusions} objects.
     *
     * @return a {@link Builder} instance
     */
    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing {@link Exclusions} objects.
     */
    public static final class Builder {

        private static final String THRESHOLD_UNITS_COUNT = "thresholdUnitsCount";

        private Exclusions template = new Exclusions();

        private Builder() {
        }

        @Nonnull
        private Exclusions template() {
            if (this.template == null) {
                throw new IllegalStateException("Exclusions object has already been built");
            }
            return this.template;
        }

        /**
         * Adds or updates the threshold for index size in bytes.
         *
         * @param indexSizeThresholdInBytes the threshold value in bytes
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withIndexSizeThreshold(final long indexSizeThresholdInBytes) {
            template().indexSizeThresholdInBytes = Validators.sizeNotNegative(indexSizeThresholdInBytes, "indexSizeThresholdInBytes");
            return this;
        }

        /**
         * Adds or updates the threshold for index size using memory units.
         *
         * @param thresholdUnitsCount the threshold value in memory units
         * @param unit                the memory unit (e.g., bytes, kilobytes)
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withIndexSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexSizeInBytes = unit.convertToBytes(Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withIndexSizeThreshold(indexSizeInBytes);
        }

        /**
         * Adds or updates the threshold for table size in bytes.
         *
         * @param tableSizeThresholdInBytes the threshold value in bytes
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withTableSizeThreshold(final long tableSizeThresholdInBytes) {
            template().tableSizeThresholdInBytes = Validators.sizeNotNegative(tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
            return this;
        }

        /**
         * Adds or updates the threshold for table size using memory units.
         *
         * @param thresholdUnitsCount the threshold value in memory units
         * @param unit                the memory unit (e.g., bytes, kilobytes)
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withTableSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long tableSizeInBytes = unit.convertToBytes(Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withTableSizeThreshold(tableSizeInBytes);
        }

        /**
         * Adds or updates the threshold for bloat size in bytes.
         *
         * @param bloatSizeThresholdInBytes the threshold value in bytes
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withBloatSizeThreshold(final long bloatSizeThresholdInBytes) {
            template().bloatSizeThresholdInBytes = Validators.sizeNotNegative(bloatSizeThresholdInBytes, "bloatSizeThresholdInBytes");
            return this;
        }

        /**
         * Adds or updates the threshold for bloat size using memory units.
         *
         * @param thresholdUnitsCount the threshold value in memory units
         * @param unit                the memory unit (e.g., bytes, kilobytes)
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexBloatSizeInBytes = unit.convertToBytes(Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withBloatSizeThreshold(indexBloatSizeInBytes);
        }

        /**
         * Adds or updates the threshold for bloat percentage.
         *
         * @param bloatPercentageThreshold the bloat percentage threshold
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withBloatPercentageThreshold(final double bloatPercentageThreshold) {
            template().bloatPercentageThreshold = Validators.validPercent(bloatPercentageThreshold, "bloatPercentageThreshold");
            return this;
        }

        /**
         * Adds index name exclusions to the configuration.
         *
         * @param indexNameExclusions a collection of index names to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withIndexes(@Nonnull final Collection<String> indexNameExclusions) {
            template().indexNameExclusions.addAll(validateExclusions(indexNameExclusions, "indexNameExclusions"));
            return this;
        }

        /**
         * Adds a single index name exclusion to the configuration.
         *
         * @param indexNameExclusion the index name to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withIndex(@Nonnull final String indexNameExclusion) {
            withIndexes(Set.of(indexNameExclusion));
            return this;
        }

        /**
         * Adds table name exclusions to the configuration.
         *
         * @param tableNameExclusions a collection of table names to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withTables(@Nonnull final Collection<String> tableNameExclusions) {
            template().tableNameExclusions.addAll(validateExclusions(tableNameExclusions, "tableNameExclusions"));
            return this;
        }

        /**
         * Adds a single table name exclusion to the configuration.
         *
         * @param tableNameExclusion the table name to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withTable(@Nonnull final String tableNameExclusion) {
            withTables(Set.of(tableNameExclusion));
            return this;
        }

        /**
         * Adds sequence name exclusions to the configuration.
         *
         * @param sequenceNameExclusions a collection of sequence names to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withSequences(@Nonnull final Collection<String> sequenceNameExclusions) {
            template().sequenceNameExclusions.addAll(validateExclusions(sequenceNameExclusions, "sequenceNameExclusions"));
            return this;
        }

        /**
         * Adds a single sequence name exclusion to the configuration.
         *
         * @param sequenceNameExclusion the sequence name to exclude
         * @return this {@link Builder} instance
         */
        @Nonnull
        public Builder withSequence(@Nonnull final String sequenceNameExclusion) {
            withSequences(Set.of(sequenceNameExclusion));
            return this;
        }

        /**
         * Builds the {@link Exclusions} object.
         *
         * @return the constructed {@link Exclusions} object
         * @throws IllegalStateException if the builder is reused after building
         */
        @Nonnull
        public Exclusions build() {
            final Exclusions exclusions = template();
            template = null;
            return exclusions;
        }

        @Nonnull
        private Set<String> validateExclusions(@Nonnull final Collection<String> exclusions,
                                               @Nonnull final String argumentName) {
            final Set<String> defensiveCopy = Set.copyOf(Objects.requireNonNull(exclusions, argumentName));
            for (final String exclusion : defensiveCopy) {
                Validators.notBlank(exclusion, argumentName);
            }
            return defensiveCopy;
        }
    }
}
