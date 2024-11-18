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

import io.github.mfvanek.pg.units.MemoryUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ExclusionsTest {

    @Test
    void parseTest() {
        final Exclusions e = Exclusions.builder()
            .withDuplicatedIndexesExclusions("i1,i2,, i3, , i4 ")
            .withIntersectedIndexesExclusions(", ,   ,, i5, i6")
            .withUnusedIndexesExclusions(",i7,i8,,i9")
            .withTablesWithMissingIndexesExclusions(",  , , s.t1, s.t2")
            .withTablesWithoutPrimaryKeyExclusions("  , , s.t3, s.t4, s.t5, t6")
            .withIndexesWithNullValuesExclusions(",,s.i1,  , , ,,,")
            .withBtreeIndexesOnArrayColumnsExclusions(",s.i2, , ,s.i3")
            .build();
        assertThat(e).isNotNull();

        assertThat(e.getDuplicatedIndexesExclusions())
            .isNotNull()
            .hasSize(4)
            .containsExactlyInAnyOrder("i1", "i2", "i3", "i4");

        assertThat(e.getIntersectedIndexesExclusions())
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder("i5", "i6");

        assertThat(e.getUnusedIndexesExclusions())
            .isNotNull()
            .hasSize(3)
            .containsExactlyInAnyOrder("i7", "i8", "i9");

        assertThat(e.getTablesWithMissingIndexesExclusions())
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder("s.t1", "s.t2");

        assertThat(e.getTablesWithoutPrimaryKeyExclusions())
            .isNotNull()
            .hasSize(4)
            .containsExactlyInAnyOrder("s.t3", "s.t4", "s.t5", "t6");

        assertThat(e.getIndexesWithNullValuesExclusions())
            .isNotNull()
            .hasSize(1)
            .containsExactlyInAnyOrder("s.i1");

        assertThat(e.getBtreeIndexesOnArrayColumnsExclusions())
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder("s.i2", "s.i3");
    }

    @Test
    void sizeInBytesTest() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(11L)
            .withTableSizeThreshold(22L)
            .withIndexBloatSizeThreshold(33L)
            .withTableBloatSizeThreshold(44L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(11L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(22L);
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isEqualTo(33L);
        assertThat(e.getTableBloatSizeThresholdInBytes()).isEqualTo(44L);
    }

    @Test
    void withMemoryUnitTest() {
        final Exclusions e = Exclusions.builder()
            .withTableSizeThreshold(10, MemoryUnit.MB)
            .withIndexSizeThreshold(2, MemoryUnit.GB)
            .withIndexBloatSizeThreshold(4, MemoryUnit.KB)
            .withTableBloatSizeThreshold(8, MemoryUnit.KB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(2_147_483_648L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(10_485_760L);
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isEqualTo(4_096L);
        assertThat(e.getTableBloatSizeThresholdInBytes()).isEqualTo(8_192L);
    }

    @Test
    void emptyTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e).isNotNull();

        assertThat(e.getDuplicatedIndexesExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getIntersectedIndexesExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getUnusedIndexesExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getTablesWithMissingIndexesExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getTablesWithoutPrimaryKeyExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getIndexesWithNullValuesExclusions())
            .isNotNull()
            .isEmpty();

        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getIndexBloatPercentageThreshold()).isZero();
        assertThat(e.getTableBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getTableBloatPercentageThreshold()).isZero();
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
            .withIndexBloatSizeThreshold(0L)
            .withTableBloatSizeThreshold(0L)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getTableBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithZeroSizeInMemoryUnits() {
        final Exclusions e = Exclusions.builder()
            .withIndexSizeThreshold(0, MemoryUnit.KB)
            .withTableSizeThreshold(0, MemoryUnit.KB)
            .withIndexBloatSizeThreshold(0, MemoryUnit.KB)
            .withTableBloatSizeThreshold(0, MemoryUnit.GB)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexSizeThresholdInBytes()).isZero();
        assertThat(e.getTableSizeThresholdInBytes()).isZero();
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isZero();
        assertThat(e.getTableBloatSizeThresholdInBytes()).isZero();
    }

    @Test
    void builderWithInvalidSize() {
        final ExclusionsBuilder builder = Exclusions.builder();
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
            .withIndexBloatPercentageThreshold(0)
            .withTableBloatPercentageThreshold(0)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexBloatPercentageThreshold()).isZero();
        assertThat(e.getTableBloatPercentageThreshold()).isZero();
    }

    @Test
    void maxPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
            .withIndexBloatPercentageThreshold(100)
            .withTableBloatPercentageThreshold(100)
            .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexBloatPercentageThreshold()).isEqualTo(100);
        assertThat(e.getTableBloatPercentageThreshold()).isEqualTo(100);
    }

    @Test
    void invalidPercentageThreshold() {
        final ExclusionsBuilder builder = Exclusions.builder();
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
