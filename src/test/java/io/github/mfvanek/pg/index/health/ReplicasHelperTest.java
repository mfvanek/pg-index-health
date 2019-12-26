/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health;

import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

class ReplicasHelperTest {

    @Test
    void getUnusedIndexesAsIntersectionResult() {
        final UnusedIndex i1 = UnusedIndex.of("t1", "i1", 1L, 1L);
        final UnusedIndex i2 = UnusedIndex.of("t1", "i2", 2L, 2L);
        final UnusedIndex i3 = UnusedIndex.of("t2", "i3", 3L, 3L);
        final UnusedIndex i4 = UnusedIndex.of("t3", "i4", 4L, 4L);
        final UnusedIndex i5 = UnusedIndex.of("t3", "i5", 5L, 5L);
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = Arrays.asList(
                Arrays.asList(i5, i4, i1, i3),
                Arrays.asList(i2, i1, i5),
                Arrays.asList(i2, i5, i1, i4)
        );
        final List<UnusedIndex> unusedIndexes = ReplicasHelper.getUnusedIndexesAsIntersectionResult(
                potentiallyUnusedIndexesFromAllHosts);
        assertThat(unusedIndexes, hasSize(2));
        assertThat(unusedIndexes, containsInAnyOrder(i1, i5));
    }

    @Test
    void getTablesWithMissingIndexesAsUnionResult() {
        final TableWithMissingIndex t1 = TableWithMissingIndex.of("t1", 1L, 10L, 1L);
        final TableWithMissingIndex t2 = TableWithMissingIndex.of("t2", 2L, 30L, 3L);
        final TableWithMissingIndex t3 = TableWithMissingIndex.of("t3", 3L, 40L, 4L);
        final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = Arrays.asList(
                Collections.emptyList(),
                Arrays.asList(t1, t3),
                Collections.singletonList(t2),
                Arrays.asList(t2, t3)
        );
        final List<TableWithMissingIndex> tablesWithMissingIndexes = ReplicasHelper.getTablesWithMissingIndexesAsUnionResult(
                tablesWithMissingIndexesFromAllHosts);
        assertThat(tablesWithMissingIndexes, hasSize(3));
        assertThat(tablesWithMissingIndexes, containsInAnyOrder(t1, t2, t3));
    }
}
