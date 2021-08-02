/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReplicasHelperTest {

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(ReplicasHelper.class));
    }

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
    void getUnusedIndexesAsIntersectionResultWithEmptyInput() {
        final List<UnusedIndex> unusedIndexes = ReplicasHelper.getUnusedIndexesAsIntersectionResult(Collections.emptyList());
        assertNotNull(unusedIndexes);
        assertThat(unusedIndexes, empty());
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

    @SuppressWarnings("ConstantConditions")
    @Test
    void getLastStatsResetDateLogMessageWithWrongArguments() {
        assertThrows(NullPointerException.class, () -> ReplicasHelper.getLastStatsResetDateLogMessage(null, null));
        assertThrows(NoSuchElementException.class, () -> ReplicasHelper.getLastStatsResetDateLogMessage(null, emptyMap()));
        assertThrows(NoSuchElementException.class, () -> ReplicasHelper.getLastStatsResetDateLogMessage(PgHostImpl.ofPrimary(), emptyMap()));
    }

    @Test
    void getLastStatsResetDateLogMessageWithoutResetTimestamp() {
        final PgHost host = PgHostImpl.ofPrimary();
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.empty());
        final String logMessage = ReplicasHelper.getLastStatsResetDateLogMessage(host, Collections.singletonMap(host, statisticsMaintenance));
        assertEquals("Statistics have never been reset on this host", logMessage);
    }

    @Test
    void getLastStatsResetDateLogMessageWithResetTimestamp() {
        final PgHost host = PgHostImpl.ofPrimary();
        final OffsetDateTime resetDate = OffsetDateTime.now();
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.of(resetDate.minusDays(123L)));
        final String logMessage = ReplicasHelper.getLastStatsResetDateLogMessage(host, Collections.singletonMap(host, statisticsMaintenance));
        assertThat(logMessage, startsWith("Last statistics reset on this host was 123 days ago ("));
    }
}
