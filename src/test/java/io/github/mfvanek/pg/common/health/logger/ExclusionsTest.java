/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertNotNull(e);

        assertNotNull(e.getDuplicatedIndexesExclusions());
        assertThat(e.getDuplicatedIndexesExclusions(), hasSize(4));
        assertThat(e.getDuplicatedIndexesExclusions(), containsInAnyOrder("i1", "i2", "i3", "i4"));

        assertNotNull(e.getIntersectedIndexesExclusions());
        assertThat(e.getIntersectedIndexesExclusions(), hasSize(2));
        assertThat(e.getIntersectedIndexesExclusions(), containsInAnyOrder("i5", "i6"));

        assertNotNull(e.getUnusedIndexesExclusions());
        assertThat(e.getUnusedIndexesExclusions(), hasSize(3));
        assertThat(e.getUnusedIndexesExclusions(), containsInAnyOrder("i7", "i8", "i9"));

        assertNotNull(e.getTablesWithMissingIndexesExclusions());
        assertThat(e.getTablesWithMissingIndexesExclusions(), hasSize(2));
        assertThat(e.getTablesWithMissingIndexesExclusions(), containsInAnyOrder("s.t1", "s.t2"));

        assertNotNull(e.getTablesWithoutPrimaryKeyExclusions());
        assertThat(e.getTablesWithoutPrimaryKeyExclusions(), hasSize(4));
        assertThat(e.getTablesWithoutPrimaryKeyExclusions(), containsInAnyOrder("s.t3", "s.t4", "s.t5", "t6"));

        assertNotNull(e.getIndexesWithNullValuesExclusions());
        assertThat(e.getIndexesWithNullValuesExclusions(), hasSize(1));
        assertThat(e.getIndexesWithNullValuesExclusions(), containsInAnyOrder("s.i1"));
    }

    @Test
    void sizeInBytesTest() {
        final Exclusions e = Exclusions.builder()
                .withIndexSizeThreshold(11L)
                .withTableSizeThreshold(22L)
                .withIndexBloatSizeThreshold(33L)
                .withTableBloatSizeThreshold(44L)
                .build();
        assertNotNull(e);
        assertEquals(11L, e.getIndexSizeThresholdInBytes());
        assertEquals(22L, e.getTableSizeThresholdInBytes());
        assertEquals(33L, e.getIndexBloatSizeThresholdInBytes());
        assertEquals(44L, e.getTableBloatSizeThresholdInBytes());
    }

    @Test
    void withMemoryUnitTest() {
        final Exclusions e = Exclusions.builder()
                .withTableSizeThreshold(10, MemoryUnit.MB)
                .withIndexSizeThreshold(2, MemoryUnit.GB)
                .withIndexBloatSizeThreshold(4, MemoryUnit.KB)
                .withTableBloatSizeThreshold(8, MemoryUnit.KB)
                .build();
        assertNotNull(e);
        assertEquals(2_147_483_648L, e.getIndexSizeThresholdInBytes());
        assertEquals(10_485_760L, e.getTableSizeThresholdInBytes());
        assertEquals(4_096L, e.getIndexBloatSizeThresholdInBytes());
        assertEquals(8_192L, e.getTableBloatSizeThresholdInBytes());
    }

    @Test
    void emptyTest() {
        final Exclusions e = Exclusions.empty();
        assertNotNull(e);

        assertNotNull(e.getDuplicatedIndexesExclusions());
        assertThat(e.getDuplicatedIndexesExclusions(), hasSize(0));

        assertNotNull(e.getIntersectedIndexesExclusions());
        assertThat(e.getIntersectedIndexesExclusions(), hasSize(0));

        assertNotNull(e.getUnusedIndexesExclusions());
        assertThat(e.getUnusedIndexesExclusions(), hasSize(0));

        assertNotNull(e.getTablesWithMissingIndexesExclusions());
        assertThat(e.getTablesWithMissingIndexesExclusions(), hasSize(0));

        assertNotNull(e.getTablesWithoutPrimaryKeyExclusions());
        assertThat(e.getTablesWithoutPrimaryKeyExclusions(), hasSize(0));

        assertNotNull(e.getIndexesWithNullValuesExclusions());
        assertThat(e.getIndexesWithNullValuesExclusions(), hasSize(0));

        assertEquals(0L, e.getIndexSizeThresholdInBytes());
        assertEquals(0L, e.getTableSizeThresholdInBytes());
        assertEquals(0L, e.getIndexBloatSizeThresholdInBytes());
        assertEquals(0, e.getIndexBloatPercentageThreshold());
        assertEquals(0L, e.getTableBloatSizeThresholdInBytes());
        assertEquals(0, e.getTableBloatPercentageThreshold());
    }

    @Test
    void toStringTest() {
        final Exclusions e = Exclusions.empty();
        assertEquals("Exclusions{duplicatedIndexesExclusions=[], " +
                        "intersectedIndexesExclusions=[], unusedIndexesExclusions=[], " +
                        "tablesWithMissingIndexesExclusions=[], tablesWithoutPrimaryKeyExclusions=[], " +
                        "indexesWithNullValuesExclusions=[], indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, " +
                        "indexBloatSizeThresholdInBytes=0, indexBloatPercentageThreshold=0, " +
                        "tableBloatSizeThresholdInBytes=0, tableBloatPercentageThreshold=0}",
                e.toString());
    }

    @Test
    void builderWithZeroSizeInBytes() {
        final Exclusions e = Exclusions.builder()
                .withIndexSizeThreshold(0L)
                .withTableSizeThreshold(0L)
                .withIndexBloatSizeThreshold(0L)
                .withTableBloatSizeThreshold(0L)
                .build();
        assertNotNull(e);
        assertEquals(0L, e.getIndexSizeThresholdInBytes());
        assertEquals(0L, e.getTableSizeThresholdInBytes());
        assertEquals(0L, e.getIndexBloatSizeThresholdInBytes());
        assertEquals(0L, e.getTableBloatSizeThresholdInBytes());
    }

    @Test
    void builderWithZeroSizeInMemoryUnits() {
        final Exclusions e = Exclusions.builder()
                .withIndexSizeThreshold(0, MemoryUnit.KB)
                .withTableSizeThreshold(0, MemoryUnit.KB)
                .withIndexBloatSizeThreshold(0, MemoryUnit.KB)
                .withTableBloatSizeThreshold(0, MemoryUnit.GB)
                .build();
        assertNotNull(e);
        assertEquals(0L, e.getIndexSizeThresholdInBytes());
        assertEquals(0L, e.getTableSizeThresholdInBytes());
        assertEquals(0L, e.getIndexBloatSizeThresholdInBytes());
        assertEquals(0L, e.getTableBloatSizeThresholdInBytes());
    }

    @Test
    void builderWithInvalidSize() {
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexSizeThreshold(-1L));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withTableSizeThreshold(-1L));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexBloatSizeThreshold(-1L));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexSizeThreshold(-1, MemoryUnit.KB));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withTableSizeThreshold(-1, MemoryUnit.KB));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexBloatSizeThreshold(-1, MemoryUnit.KB));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withTableBloatSizeThreshold(-1, MemoryUnit.MB));
    }

    @Test
    void zeroPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
                .withIndexBloatPercentageThreshold(0)
                .withTableBloatPercentageThreshold(0)
                .build();
        assertNotNull(e);
        assertEquals(0, e.getIndexBloatPercentageThreshold());
        assertEquals(0, e.getTableBloatPercentageThreshold());
    }

    @Test
    void maxPercentageThreshold() {
        final Exclusions e = Exclusions.builder()
                .withIndexBloatPercentageThreshold(100)
                .withTableBloatPercentageThreshold(100)
                .build();
        assertNotNull(e);
        assertEquals(100, e.getIndexBloatPercentageThreshold());
        assertEquals(100, e.getTableBloatPercentageThreshold());
    }

    @Test
    void invalidPercentageThreshold() {
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexBloatPercentageThreshold(-1));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withIndexBloatPercentageThreshold(101));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withTableBloatPercentageThreshold(-1));
        assertThrows(IllegalArgumentException.class, () -> Exclusions.builder().withTableBloatPercentageThreshold(101));
    }
}
