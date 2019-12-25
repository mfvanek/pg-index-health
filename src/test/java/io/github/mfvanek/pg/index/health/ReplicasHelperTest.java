/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.health;

import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

class ReplicasHelperTest {

    @Test
    void getUnusedIndexesAsIntersectionResult() {
        final var i1 = UnusedIndex.of("t1", "i1", 1L, 1L);
        final var i2 = UnusedIndex.of("t1", "i2", 2L, 2L);
        final var i3 = UnusedIndex.of("t2", "i3", 3L, 3L);
        final var i4 = UnusedIndex.of("t3", "i4", 4L, 4L);
        final var i5 = UnusedIndex.of("t3", "i5", 5L, 5L);
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = List.of(
                List.of(i5, i4, i1, i3),
                List.of(i2, i1, i5),
                List.of(i2, i5, i1, i4)
        );
        final var unusedIndexes = ReplicasHelper.getUnusedIndexesAsIntersectionResult(potentiallyUnusedIndexesFromAllHosts);
        assertThat(unusedIndexes, hasSize(2));
        assertThat(unusedIndexes, containsInAnyOrder(i1, i5));
    }

    @Test
    void getTablesWithMissingIndexesAsUnionResult() {
        final var t1 = TableWithMissingIndex.of("t1", 1L, 10L, 1L);
        final var t2 = TableWithMissingIndex.of("t2", 2L, 30L, 3L);
        final var t3 = TableWithMissingIndex.of("t3", 3L, 40L, 4L);
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = List.of(
                List.of(),
                List.of(t1, t3),
                List.of(t2),
                List.of(t2, t3)
        );
        final var tablesWithMissingIndexes = ReplicasHelper.getTablesWithMissingIndexesAsUnionResult(
                tablesWithMissingIndexesFromAllHosts);
        assertThat(tablesWithMissingIndexes, hasSize(3));
        assertThat(tablesWithMissingIndexes, containsInAnyOrder(t1, t2, t3));
    }
}
