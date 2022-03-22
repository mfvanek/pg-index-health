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

import io.github.mfvanek.pg.model.MemoryUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
                .build();
        assertThat(e).isNotNull();

        assertThat(e.getDuplicatedIndexesExclusions()).isNotNull();
        assertThat(e.getDuplicatedIndexesExclusions()).hasSize(4);
        assertThat(e.getDuplicatedIndexesExclusions()).containsExactlyInAnyOrder("i1", "i2", "i3", "i4");

        assertThat(e.getIntersectedIndexesExclusions()).isNotNull();
        assertThat(e.getIntersectedIndexesExclusions()).hasSize(2);
        assertThat(e.getIntersectedIndexesExclusions()).containsExactlyInAnyOrder("i5", "i6");

        assertThat(e.getUnusedIndexesExclusions()).isNotNull();
        assertThat(e.getUnusedIndexesExclusions()).hasSize(3);
        assertThat(e.getUnusedIndexesExclusions()).containsExactlyInAnyOrder("i7", "i8", "i9");

        assertThat(e.getTablesWithMissingIndexesExclusions()).isNotNull();
        assertThat(e.getTablesWithMissingIndexesExclusions()).hasSize(2);
        assertThat(e.getTablesWithMissingIndexesExclusions()).containsExactlyInAnyOrder("s.t1", "s.t2");

        assertThat(e.getTablesWithoutPrimaryKeyExclusions()).isNotNull();
        assertThat(e.getTablesWithoutPrimaryKeyExclusions()).hasSize(4);
        assertThat(e.getTablesWithoutPrimaryKeyExclusions()).containsExactlyInAnyOrder("s.t3", "s.t4", "s.t5", "t6");

        assertThat(e.getIndexesWithNullValuesExclusions()).isNotNull();
        assertThat(e.getIndexesWithNullValuesExclusions()).hasSize(1);
        assertThat(e.getIndexesWithNullValuesExclusions()).containsExactlyInAnyOrder("s.i1");
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

        assertThat(e.getDuplicatedIndexesExclusions()).isNotNull();
        assertThat(e.getDuplicatedIndexesExclusions()).hasSize(0);

        assertThat(e.getIntersectedIndexesExclusions()).isNotNull();
        assertThat(e.getIntersectedIndexesExclusions()).hasSize(0);

        assertThat(e.getUnusedIndexesExclusions()).isNotNull();
        assertThat(e.getUnusedIndexesExclusions()).hasSize(0);

        assertThat(e.getTablesWithMissingIndexesExclusions()).isNotNull();
        assertThat(e.getTablesWithMissingIndexesExclusions()).hasSize(0);

        assertThat(e.getTablesWithoutPrimaryKeyExclusions()).isNotNull();
        assertThat(e.getTablesWithoutPrimaryKeyExclusions()).hasSize(0);

        assertThat(e.getIndexesWithNullValuesExclusions()).isNotNull();
        assertThat(e.getIndexesWithNullValuesExclusions()).hasSize(0);

        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getIndexBloatPercentageThreshold()).isEqualTo(0);
        assertThat(e.getTableBloatSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableBloatPercentageThreshold()).isEqualTo(0);
    }

    @Test
    void toStringTest() {
        final Exclusions e = Exclusions.empty();
        assertThat(e.toString()).isEqualTo("Exclusions{duplicatedIndexesExclusions=[], " + "intersectedIndexesExclusions=[], unusedIndexesExclusions=[], " + 
                "tablesWithMissingIndexesExclusions=[], tablesWithoutPrimaryKeyExclusions=[], " + "indexesWithNullValuesExclusions=[], indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, " + 
                "indexBloatSizeThresholdInBytes=0, indexBloatPercentageThreshold=0, " + "tableBloatSizeThresholdInBytes=0, tableBloatPercentageThreshold=0}")
        ;
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
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableBloatSizeThresholdInBytes()).isEqualTo(0L);
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
        assertThat(e.getIndexSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getIndexBloatSizeThresholdInBytes()).isEqualTo(0L);
        assertThat(e.getTableBloatSizeThresholdInBytes()).isEqualTo(0L);
    }

    @Test
    void builderWithInvalidSize() {
        assertThatThrownBy(() -> Exclusions.builder().withIndexSizeThreshold(-1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withTableSizeThreshold(-1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withIndexBloatSizeThreshold(-1L)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withIndexSizeThreshold(-1, MemoryUnit.KB)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withTableSizeThreshold(-1, MemoryUnit.KB)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withIndexBloatSizeThreshold(-1, MemoryUnit.KB)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withTableBloatSizeThreshold(-1, MemoryUnit.MB)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void zeroPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
                .withIndexBloatPercentageThreshold(0)
                .withTableBloatPercentageThreshold(0)
                .build();
        assertThat(e).isNotNull();
        assertThat(e.getIndexBloatPercentageThreshold()).isEqualTo(0);
        assertThat(e.getTableBloatPercentageThreshold()).isEqualTo(0);
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
        assertThatThrownBy(() -> Exclusions.builder().withIndexBloatPercentageThreshold(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withIndexBloatPercentageThreshold(101)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withTableBloatPercentageThreshold(-1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Exclusions.builder().withTableBloatPercentageThreshold(101)).isInstanceOf(IllegalArgumentException.class);
    }
}
