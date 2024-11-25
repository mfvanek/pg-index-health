/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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
 *
 * @author Ivan Vakhrushev
 * @see HealthLogger
 * @see AbstractHealthLogger
 */
public final class Exclusions {

    private final Set<String> indexNameExclusions;
    private final Set<String> tableNameExclusions;
    private final Set<String> sequenceNameExclusions;
    private long indexSizeThresholdInBytes;
    private long tableSizeThresholdInBytes;
    private long bloatSizeThresholdInBytes;
    private double bloatPercentageThreshold;

    private Exclusions() {
        this.indexNameExclusions = new HashSet<>();
        this.tableNameExclusions = new HashSet<>();
        this.sequenceNameExclusions = new HashSet<>();
    }

    public double getBloatPercentageThreshold() {
        return bloatPercentageThreshold;
    }

    public long getBloatSizeThresholdInBytes() {
        return bloatSizeThresholdInBytes;
    }

    public long getTableSizeThresholdInBytes() {
        return tableSizeThresholdInBytes;
    }

    public long getIndexSizeThresholdInBytes() {
        return indexSizeThresholdInBytes;
    }

    @Nonnull
    public Collection<String> getTableNameExclusions() {
        return Set.copyOf(tableNameExclusions);
    }

    @Nonnull
    public Collection<String> getIndexNameExclusions() {
        return Set.copyOf(indexNameExclusions);
    }

    @Nonnull
    public Collection<String> getSequenceNameExclusions() {
        return Set.copyOf(sequenceNameExclusions);
    }

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

    public static final class Builder {

        private static final String THRESHOLD_UNITS_COUNT = "thresholdUnitsCount";

        private Exclusions template = new Exclusions();

        private Builder() {}

        @Nonnull
        public Builder withIndexSizeThreshold(final long indexSizeThresholdInBytes) {
            this.template.indexSizeThresholdInBytes = Validators.sizeNotNegative(indexSizeThresholdInBytes, "indexSizeThresholdInBytes");
            return this;
        }

        @Nonnull
        public Builder withIndexSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withIndexSizeThreshold(indexSizeInBytes);
        }

        @Nonnull
        public Builder withTableSizeThreshold(final long tableSizeThresholdInBytes) {
            this.template.tableSizeThresholdInBytes = Validators.sizeNotNegative(tableSizeThresholdInBytes, "tableSizeThresholdInBytes");
            return this;
        }

        @Nonnull
        public Builder withTableSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long tableSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withTableSizeThreshold(tableSizeInBytes);
        }

        @Nonnull
        public Builder withBloatSizeThreshold(final long bloatSizeThresholdInBytes) {
            this.template.bloatSizeThresholdInBytes = Validators.sizeNotNegative(bloatSizeThresholdInBytes, "bloatSizeThresholdInBytes");
            return this;
        }

        @Nonnull
        public Builder withBloatSizeThreshold(final int thresholdUnitsCount, final MemoryUnit unit) {
            final long indexBloatSizeInBytes = unit.convertToBytes(
                Validators.argumentNotNegative(thresholdUnitsCount, THRESHOLD_UNITS_COUNT));
            return withBloatSizeThreshold(indexBloatSizeInBytes);
        }

        @Nonnull
        public Builder withBloatPercentageThreshold(final double bloatPercentageThreshold) {
            this.template.bloatPercentageThreshold = Validators.validPercent(bloatPercentageThreshold, "bloatPercentageThreshold");
            return this;
        }

        @Nonnull
        public Builder withIndexes(@Nonnull final Collection<String> indexNameExclusions) {
            this.template.indexNameExclusions.addAll(Objects.requireNonNull(indexNameExclusions, "indexNameExclusions"));
            return this;
        }

        @Nonnull
        public Builder withIndex(@Nonnull final String indexNameExclusion) {
            this.template.indexNameExclusions.add(Validators.notBlank(indexNameExclusion, "indexNameExclusion"));
            return this;
        }

        @Nonnull
        public Builder withTables(@Nonnull final Collection<String> tableNameExclusions) {
            this.template.tableNameExclusions.addAll(Objects.requireNonNull(tableNameExclusions, "tableNameExclusions"));
            return this;
        }

        @Nonnull
        public Builder withTable(@Nonnull final String tableNameExclusion) {
            this.template.tableNameExclusions.add(Validators.notBlank(tableNameExclusion, "tableNameExclusion"));
            return this;
        }

        @Nonnull
        public Builder withSequences(@Nonnull final Collection<String> sequenceNameExclusions) {
            this.template.sequenceNameExclusions.addAll(Objects.requireNonNull(sequenceNameExclusions, "sequenceNameExclusions"));
            return this;
        }

        @Nonnull
        public Builder withSequence(@Nonnull final String sequenceNameExclusion) {
            this.template.sequenceNameExclusions.add(Validators.notBlank(sequenceNameExclusion, "sequenceNameExclusion"));
            return this;
        }

        @Nonnull
        public Exclusions build() {
            if (template == null) {
                throw new IllegalStateException("Method build() cannot be called twice");
            }
            final Exclusions exclusions = template;
            template = null;
            return exclusions;
        }
    }

    /**
     * Returns empty exclusions list.
     *
     * @return empty {@code Exclusions} object
     */
    @Nonnull
    public static Exclusions empty() {
        return builder().build();
    }

    /**
     * Returns a {@code Builder} for constructing {@link Exclusions} object.
     *
     * @return {@code Builder}
     */
    @Nonnull
    public static Exclusions.Builder builder() {
        return new Builder();
    }
}
