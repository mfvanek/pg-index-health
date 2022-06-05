/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.utils.ClockHolder;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReplicasHelperTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ReplicasHelper.class))
                .isInstanceOf(UnsupportedOperationException.class);
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
        assertThat(unusedIndexes)
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(i1, i5);
    }

    @Test
    void getUnusedIndexesAsIntersectionResultWithEmptyInput() {
        final List<UnusedIndex> unusedIndexes = ReplicasHelper.getUnusedIndexesAsIntersectionResult(Collections.emptyList());
        assertThat(unusedIndexes)
                .isNotNull()
                .isEmpty();
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
        assertThat(tablesWithMissingIndexes)
                .hasSize(3)
                .containsExactlyInAnyOrder(t1, t2, t3);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getLastStatsResetDateLogMessageWithWrongArguments() {
        assertThatThrownBy(() -> ReplicasHelper.getLastStatsResetDateLogMessage(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("statisticsMaintenanceForAllHosts cannot be null");
        assertThatThrownBy(() -> ReplicasHelper.getLastStatsResetDateLogMessage(null, emptyMap()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("StatisticsMaintenanceOnHost object wasn't found for host null");
        assertThatThrownBy(() -> ReplicasHelper.getLastStatsResetDateLogMessage(PgHostImpl.ofPrimary(), emptyMap()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("StatisticsMaintenanceOnHost object wasn't found for host PgHostImpl{pgUrl='jdbc:postgresql://primary', hostNames=[primary], maybePrimary=true}");
    }

    @Test
    void getLastStatsResetDateLogMessageWithoutResetTimestamp() {
        final PgHost host = PgHostImpl.ofPrimary();
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.empty());
        final String logMessage = ReplicasHelper.getLastStatsResetDateLogMessage(host, Collections.singletonMap(host, statisticsMaintenance));
        assertThat(logMessage).isEqualTo("Statistics have never been reset on this host");
    }

    @Test
    void getLastStatsResetDateLogMessageWithResetTimestamp() {
        final PgHost host = PgHostImpl.ofPrimary();
        final OffsetDateTime resetDate = OffsetDateTime.now(ClockHolder.clock());
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.of(resetDate.minusDays(123L)));
        final String logMessage = ReplicasHelper.getLastStatsResetDateLogMessage(host, Collections.singletonMap(host, statisticsMaintenance));
        assertThat(logMessage).startsWith("Last statistics reset on this host was 123 days ago (");
    }
}
