/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

final class ReplicasHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicasHelper.class);

    private ReplicasHelper() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static List<UnusedIndex> getUnusedIndexesAsIntersectionResult(
            @Nonnull final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts) {
        LOGGER.debug("potentiallyUnusedIndexesFromAllHosts = {}", potentiallyUnusedIndexesFromAllHosts);
        Collection<UnusedIndex> unusedIndexes = null;
        for (List<UnusedIndex> unusedIndexesFromHost : potentiallyUnusedIndexesFromAllHosts) {
            if (unusedIndexes == null) {
                unusedIndexes = unusedIndexesFromHost;
            }
            unusedIndexes = CollectionUtils.intersection(unusedIndexes, unusedIndexesFromHost);
        }
        final List<UnusedIndex> result = unusedIndexes == null ? Collections.emptyList() : new ArrayList<>(unusedIndexes);
        LOGGER.debug("Intersection result {}", result);
        return result;
    }

    @Nonnull
    static List<TableWithMissingIndex> getTablesWithMissingIndexesAsUnionResult(
            @Nonnull final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts) {
        LOGGER.debug("tablesWithMissingIndexesFromAllHosts = {}", tablesWithMissingIndexesFromAllHosts);
        final List<TableWithMissingIndex> result = tablesWithMissingIndexesFromAllHosts.stream()
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        LOGGER.debug("Union result {}", result);
        return result;
    }

    @Nonnull
    static String getLastStatsResetDateLogMessage(
            @Nonnull final PgHost host,
            @Nonnull final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHosts) {
        final StatisticsMaintenanceOnHost statisticsMaintenance = statisticsMaintenanceForAllHosts.get(host);
        if (statisticsMaintenance == null) {
            throw new NoSuchElementException("StatisticsMaintenanceOnHost object wasn't found for host " + host);
        }

        final Optional<OffsetDateTime> statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        if (statsResetTimestamp.isPresent()) {
            final long daysBetween = ChronoUnit.DAYS.between(statsResetTimestamp.get(), OffsetDateTime.now());
            return String.format("Last statistics reset on this host was %d days ago (%s)",
                    daysBetween, statsResetTimestamp.get().toString());
        }
        return "Statistics have never been reset on this host";
    }
}
