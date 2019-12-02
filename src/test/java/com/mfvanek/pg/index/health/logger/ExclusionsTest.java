/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExclusionsTest {

    @Test
    void parseTest() {
        final var exclusions = Exclusions.builder()
                .withDuplicatedIndicesExclusions("i1,i2,, i3, , i4 ")
                .build();
        assertNotNull(exclusions);
        assertNotNull(exclusions.getDuplicatedIndicesExclusions());
        assertThat(exclusions.getDuplicatedIndicesExclusions(), hasSize(4));
        assertThat(exclusions.getDuplicatedIndicesExclusions(), containsInAnyOrder("i1", "i2", "i3", "i4"));
    }

    @Test
    void emptyTest() {
        final var exclusions = Exclusions.empty();
        assertNotNull(exclusions);

        assertNotNull(exclusions.getDuplicatedIndicesExclusions());
        assertThat(exclusions.getDuplicatedIndicesExclusions(), hasSize(0));

        assertNotNull(exclusions.getIntersectedIndicesExclusions());
        assertThat(exclusions.getIntersectedIndicesExclusions(), hasSize(0));

        assertNotNull(exclusions.getUnusedIndicesExclusions());
        assertThat(exclusions.getUnusedIndicesExclusions(), hasSize(0));

        assertNotNull(exclusions.getTablesWithMissingIndicesExclusions());
        assertThat(exclusions.getTablesWithMissingIndicesExclusions(), hasSize(0));

        assertNotNull(exclusions.getTablesWithoutPrimaryKeyExclusions());
        assertThat(exclusions.getTablesWithoutPrimaryKeyExclusions(), hasSize(0));

        assertNotNull(exclusions.getIndicesWithNullValuesExclusions());
        assertThat(exclusions.getIndicesWithNullValuesExclusions(), hasSize(0));
    }
}
