/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExclusionsTest {

    @Test
    void parseTest() {
        final var exclusions = Exclusions.builder()
                .withDuplicatedIndexesExclusions("i1,i2,, i3, , i4 ")
                .build();
        assertNotNull(exclusions);
        assertNotNull(exclusions.getDuplicatedIndexesExclusions());
        assertThat(exclusions.getDuplicatedIndexesExclusions(), hasSize(4));
        assertThat(exclusions.getDuplicatedIndexesExclusions(), containsInAnyOrder("i1", "i2", "i3", "i4"));
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
    }

    @Test
    void toStringTest() {
        final var exclusions = Exclusions.empty();
        assertEquals("Exclusions{duplicatedIndexesExclusions=[], " +
                "intersectedIndexesExclusions=[], unusedIndexesExclusions=[], " +
                "tablesWithMissingIndexesExclusions=[], tablesWithoutPrimaryKeyExclusions=[], " +
                "indexesWithNullValuesExclusions=[], indexSizeThreshold=0}", exclusions.toString());
    }
}
