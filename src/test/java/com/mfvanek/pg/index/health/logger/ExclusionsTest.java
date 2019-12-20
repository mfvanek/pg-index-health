/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.model.MemoryUnit;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExclusionsTest {

    @Test
    void parseTest() {
        final var exclusions = Exclusions.builder()
                .withDuplicatedIndexesExclusions("i1,i2,, i3, , i4 ")
                .withIndexSizeThreshold(11L)
                .withTableSizeThreshold(22L)
                .build();
        assertNotNull(exclusions);
        assertNotNull(exclusions.getDuplicatedIndexesExclusions());
        assertThat(exclusions.getDuplicatedIndexesExclusions(), hasSize(4));
        assertThat(exclusions.getDuplicatedIndexesExclusions(), containsInAnyOrder("i1", "i2", "i3", "i4"));
        assertThat(exclusions.getIndexSizeThresholdInBytes(), equalTo(11L));
        assertThat(exclusions.getTableSizeThresholdInBytes(), equalTo(22L));
    }

    @Test
    void withMemoryUnitTest() {
        final var exclusions = Exclusions.builder()
                .withTableSizeThreshold(10, MemoryUnit.MB)
                .withIndexSizeThreshold(2, MemoryUnit.GB)
                .build();
        assertNotNull(exclusions);
        assertThat(exclusions.getIndexSizeThresholdInBytes(), equalTo(2_147_483_648L));
        assertThat(exclusions.getTableSizeThresholdInBytes(), equalTo(10_485_760L));
    }

    @Test
    void emptyTest() {
        final var exclusions = Exclusions.empty();
        assertNotNull(exclusions);

        assertNotNull(exclusions.getDuplicatedIndexesExclusions());
        assertThat(exclusions.getDuplicatedIndexesExclusions(), hasSize(0));

        assertNotNull(exclusions.getIntersectedIndexesExclusions());
        assertThat(exclusions.getIntersectedIndexesExclusions(), hasSize(0));

        assertNotNull(exclusions.getUnusedIndexesExclusions());
        assertThat(exclusions.getUnusedIndexesExclusions(), hasSize(0));

        assertNotNull(exclusions.getTablesWithMissingIndexesExclusions());
        assertThat(exclusions.getTablesWithMissingIndexesExclusions(), hasSize(0));

        assertNotNull(exclusions.getTablesWithoutPrimaryKeyExclusions());
        assertThat(exclusions.getTablesWithoutPrimaryKeyExclusions(), hasSize(0));

        assertNotNull(exclusions.getIndexesWithNullValuesExclusions());
        assertThat(exclusions.getIndexesWithNullValuesExclusions(), hasSize(0));

        assertThat(exclusions.getIndexSizeThresholdInBytes(), equalTo(0L));
        assertThat(exclusions.getTableSizeThresholdInBytes(), equalTo(0L));
    }

    @Test
    void toStringTest() {
        final var exclusions = Exclusions.empty();
        assertEquals("Exclusions{duplicatedIndexesExclusions=[], " +
                        "intersectedIndexesExclusions=[], unusedIndexesExclusions=[], " +
                        "tablesWithMissingIndexesExclusions=[], tablesWithoutPrimaryKeyExclusions=[], " +
                        "indexesWithNullValuesExclusions=[], indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0}",
                exclusions.toString());
    }

    @Test
    void toStringBuilderTest() {
        final var builder = Exclusions.builder();
        assertNotNull(builder);
        assertEquals("Builder{duplicatedIndexesExclusions='', intersectedIndexesExclusions='', " +
                        "unusedIndexesExclusions='', tablesWithMissingIndexesExclusions='', tablesWithoutPrimaryKeyExclusions='', " +
                        "indexesWithNullValuesExclusions='', indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0}",
                builder.toString());
    }
}
