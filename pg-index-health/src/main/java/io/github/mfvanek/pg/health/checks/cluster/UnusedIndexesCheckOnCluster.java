/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.core.utils.ClockHolder;
import io.github.mfvanek.pg.health.utils.CollectionUtils;
import io.github.mfvanek.pg.model.index.UnusedIndex;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Check for unused indexes on all hosts in the cluster.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class UnusedIndexesCheckOnCluster extends AbstractCheckOnCluster<UnusedIndex> {

    private static final Logger LOGGER = Logger.getLogger(UnusedIndexesCheckOnCluster.class.getName());

    private final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statistics;

    public UnusedIndexesCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, UnusedIndexesCheckOnHost::new, UnusedIndexesCheckOnCluster::getResultAsIntersection);
        this.statisticsOnHostFactory = StatisticsMaintenanceOnHostImpl::new;
        this.statistics = new HashMap<>();
    }

    @Override
    protected void doBeforeExecuteOnHost(final PgConnection connectionToHost) {
        logLastStatsResetDate(connectionToHost);
        super.doBeforeExecuteOnHost(connectionToHost);
    }

    private void logLastStatsResetDate(final PgConnection connectionToHost) {
        final String resetDateLogMessage = getLastStatsResetDateLogMessage(computeStatisticsForHostIfNeed(connectionToHost));
        LOGGER.info(resetDateLogMessage);
    }

    static List<UnusedIndex> getResultAsIntersection(
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts) {
        LOGGER.fine(() -> "potentiallyUnusedIndexesFromAllHosts = " + potentiallyUnusedIndexesFromAllHosts);
        Collection<UnusedIndex> unusedIndexes = null;
        for (final List<UnusedIndex> unusedIndexesFromHost : potentiallyUnusedIndexesFromAllHosts) {
            if (unusedIndexes == null) {
                unusedIndexes = unusedIndexesFromHost;
                continue;
            }
            unusedIndexes = CollectionUtils.intersection(unusedIndexes, unusedIndexesFromHost);
        }
        final List<UnusedIndex> result = unusedIndexes == null ? List.of() : List.copyOf(unusedIndexes);
        LOGGER.fine(() -> "Intersection result " + result);
        return result;
    }

    static String getLastStatsResetDateLogMessage(final StatisticsMaintenanceOnHost statisticsMaintenance) {
        Objects.requireNonNull(statisticsMaintenance, "statisticsMaintenance cannot be null");
        final Optional<OffsetDateTime> statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        if (statsResetTimestamp.isPresent()) {
            final long daysBetween = ChronoUnit.DAYS.between(statsResetTimestamp.get(), OffsetDateTime.now(ClockHolder.clock()));
            return String.format(Locale.ROOT, "Last statistics reset on this host was %d days ago (%s)", daysBetween, statsResetTimestamp.get());
        }
        return "Statistics have never been reset on this host";
    }

    private StatisticsMaintenanceOnHost computeStatisticsForHostIfNeed(final PgConnection connectionToHost) {
        return statistics.computeIfAbsent(connectionToHost.getHost(), h -> statisticsOnHostFactory.apply(connectionToHost));
    }
}
