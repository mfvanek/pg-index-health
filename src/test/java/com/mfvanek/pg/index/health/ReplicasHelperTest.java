/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

class ReplicasHelperTest {

    @Test
    void getUnusedIndicesAsIntersectionResult() {
        final var i1 = UnusedIndex.of("t1", "i1", 1L, 1L);
        final var i2 = UnusedIndex.of("t1", "i2", 2L, 2L);
        final var i3 = UnusedIndex.of("t2", "i3", 3L, 3L);
        final var i4 = UnusedIndex.of("t3", "i4", 4L, 4L);
        final var i5 = UnusedIndex.of("t3", "i5", 5L, 5L);
        final List<List<UnusedIndex>> potentiallyUnusedIndicesFromAllHosts = List.of(
                List.of(i5, i4, i1, i3),
                List.of(i2, i1, i5),
                List.of(i2, i5, i1, i4)
        );
        final var unusedIndices = ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndicesFromAllHosts);
        assertThat(unusedIndices, hasSize(2));
        assertThat(unusedIndices, containsInAnyOrder(i1, i5));
    }

    @Test
    void getTablesWithMissingIndicesAsUnionResult() {
        final var t1 = TableWithMissingIndex.of("t1", 10L, 1L);
        final var t2 = TableWithMissingIndex.of("t2", 30L, 3L);
        final var t3 = TableWithMissingIndex.of("t3", 40L, 4L);
        final List<List<TableWithMissingIndex>> tablesWithMissingIndicesFromAllHosts = List.of(
                List.of(),
                List.of(t1, t3),
                List.of(t2),
                List.of(t2, t3)
        );
        final var tablesWithMissingIndices = ReplicasHelper.getTablesWithMissingIndexesAsUnionResult(
                tablesWithMissingIndicesFromAllHosts);
        assertThat(tablesWithMissingIndices, hasSize(3));
        assertThat(tablesWithMissingIndices, containsInAnyOrder(t1, t2, t3));
    }
}
