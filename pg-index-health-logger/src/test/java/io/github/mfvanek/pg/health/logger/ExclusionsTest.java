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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ExclusionsTest {

    @Test
    void sizeInBytesTest() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(11L)
            .withTableSizeThreshold(22L)
            .withBloatSizeThreshold(33L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(11L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(22L);
        assertThat(e.getBloatSizeThresholdInBytes()).isEqualTo(33L);
    }

    @Test
    void withMemoryUnitTest() {
        final Exclusions e = Exclusions.builder()
            .withTableSizeThreshold(10, MemoryUnit.MB)
            .withIndexSizeThreshold(2, MemoryUnit.GB)
            .withBloatSizeThreshold(4, MemoryUnit.KB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(2_147_483_648L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(10_485_760L);
        assertThat(e.getBloatSizeThresholdInBytes()).isEqualTo(4_096L);
    }

    @Test
    void emptyTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e).isNotNull();

        assertThat(e.getIndexNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getTableNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getSequenceNameExclusions())
            .isUnmodifiable()
            .isEmpty();

        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatPercentageThreshold()).isZero();
    }

    @Test
    void toStringTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e)
            .hasToString("Exclusions{duplicatedIndexesExclusions=[], " + "intersectedIndexesExclusions=[], unusedIndexesExclusions=[], " +
                "tablesWithMissingIndexesExclusions=[], tablesWithoutPrimaryKeyExclusions=[], " +
                "indexesWithNullValuesExclusions=[], btreeIndexesOnArrayColumnsExclusions=[], " +
                "indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, " +
                "indexBloatSizeThresholdInBytes=0, indexBloatPercentageThreshold=0.0, " + "tableBloatSizeThresholdInBytes=0, tableBloatPercentageThreshold=0.0}");
    }

    @Test
    void builderWithZeroSizeInBytes() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(0L)
            .withTableSizeThreshold(0L)
            .withBloatSizeThreshold(0L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithZeroSizeInMemoryUnits() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(0, MemoryUnit.KB)
            .withTableSizeThreshold(0, MemoryUnit.KB)
            .withBloatSizeThreshold(0, MemoryUnit.KB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithInvalidSize() {
        final Exclusions.Builder builder = Exclusions.builder();
        assertThatThrownBy(() -> builder.withIndexSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withTableSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withIndexBloatSizeThreshold(-1L))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withIndexSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withTableSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withIndexBloatSizeThreshold(-1, MemoryUnit.KB))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> builder.withTableBloatSizeThreshold(-1, MemoryUnit.MB))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
            .withBloatPercentageThreshold(0)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getBloatPercentageThreshold()).isZero();
    }

    @Test
    void maxPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
            .withBloatPercentageThreshold(100)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getBloatPercentageThreshold()).isEqualTo(100);
    }

    @Test
    void invalidPercentageThreshold() {
        final Exclusions.Builder builder = Exclusions.builder();
        assertThatThrownBy(() -> builder.withIndexBloatPercentageThreshold(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexBloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> builder.withIndexBloatPercentageThreshold(101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexBloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> builder.withTableBloatPercentageThreshold(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableBloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
        assertThatThrownBy(() -> builder.withTableBloatPercentageThreshold(101))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableBloatPercentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }
}
