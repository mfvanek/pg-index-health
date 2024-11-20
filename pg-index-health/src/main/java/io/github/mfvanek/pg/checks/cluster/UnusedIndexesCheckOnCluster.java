/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.utils.ClockHolder;
import io.github.mfvanek.pg.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import javax.annotation.Nonnull;

/**
 * Check for unused indexes on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class UnusedIndexesCheckOnCluster extends AbstractCheckOnCluster<UnusedIndex> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnusedIndexesCheckOnCluster.class);

    private final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statistics;

    public UnusedIndexesCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, UnusedIndexesCheckOnHost::new, UnusedIndexesCheckOnCluster::getResultAsIntersection);
        this.statisticsOnHostFactory = StatisticsMaintenanceOnHostImpl::new;
        this.statistics = new HashMap<>();
    }

    @Override
    protected void doBeforeExecuteOnHost(@Nonnull final PgConnection connectionToHost) {
        logLastStatsResetDate(connectionToHost);
        super.doBeforeExecuteOnHost(connectionToHost);
    }

    private void logLastStatsResetDate(@Nonnull final PgConnection connectionToHost) {
        final String resetDateLogMessage = getLastStatsResetDateLogMessage(computeStatisticsForHostIfNeed(connectionToHost));
        LOGGER.info("{}", resetDateLogMessage);
    }

    @Nonnull
    static List<UnusedIndex> getResultAsIntersection(
        @Nonnull final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts) {
        LOGGER.debug("potentiallyUnusedIndexesFromAllHosts = {}", potentiallyUnusedIndexesFromAllHosts);
        Collection<UnusedIndex> unusedIndexes = null;
        for (final List<UnusedIndex> unusedIndexesFromHost : potentiallyUnusedIndexesFromAllHosts) {
            if (unusedIndexes == null) {
                unusedIndexes = unusedIndexesFromHost;
                continue;
            }
            unusedIndexes = CollectionUtils.intersection(unusedIndexes, unusedIndexesFromHost);
        }
        final List<UnusedIndex> result = unusedIndexes == null ? List.of() : List.copyOf(unusedIndexes);
        LOGGER.debug("Intersection result {}", result);
        return result;
    }

    @Nonnull
    static String getLastStatsResetDateLogMessage(@Nonnull final StatisticsMaintenanceOnHost statisticsMaintenance) {
        Objects.requireNonNull(statisticsMaintenance, "statisticsMaintenance cannot be null");
        final Optional<OffsetDateTime> statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        if (statsResetTimestamp.isPresent()) {
            final long daysBetween = ChronoUnit.DAYS.between(statsResetTimestamp.get(), OffsetDateTime.now(ClockHolder.clock()));
            return String.format(Locale.ROOT, "Last statistics reset on this host was %d days ago (%s)", daysBetween, statsResetTimestamp.get());
        }
        return "Statistics have never been reset on this host";
    }

    @Nonnull
    private StatisticsMaintenanceOnHost computeStatisticsForHostIfNeed(@Nonnull final PgConnection connectionToHost) {
        return statistics.computeIfAbsent(connectionToHost.getHost(), h -> statisticsOnHostFactory.apply(connectionToHost));
    }
}
