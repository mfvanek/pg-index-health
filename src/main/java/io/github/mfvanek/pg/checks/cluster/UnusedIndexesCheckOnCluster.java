/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.MaintenanceFactoryImpl;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Check for unused indexes on all hosts in the cluster.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class UnusedIndexesCheckOnCluster extends AbstractCheckOnCluster<UnusedIndex> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnusedIndexesCheckOnCluster.class);

    private final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHostsInCluster;

    public UnusedIndexesCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, UnusedIndexesCheckOnHost::new, UnusedIndexesCheckOnCluster::getResultAsIntersection);
        // TODO refactor this
        this.statisticsMaintenanceForAllHostsInCluster = new MaintenanceFactoryImpl().forStatistics(haPgConnection.getConnectionsToAllHostsInCluster());
    }

    @Override
    protected void doBeforeExecuteOnHost(@Nonnull final PgConnection connectionToHost) {
        logLastStatsResetDate(connectionToHost);
        super.doBeforeExecuteOnHost(connectionToHost);
    }

    private void logLastStatsResetDate(@Nonnull final PgConnection connectionToHost) {
        final String resetDateLogMessage = getLastStatsResetDateLogMessage(connectionToHost.getHost(), statisticsMaintenanceForAllHostsInCluster);
        LOGGER.info(resetDateLogMessage);
    }

    @Nonnull
    static List<UnusedIndex> getResultAsIntersection(
            @Nonnull final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts) {
        LOGGER.debug("potentiallyUnusedIndexesFromAllHosts = {}", potentiallyUnusedIndexesFromAllHosts);
        Collection<UnusedIndex> unusedIndexes = null;
        for (final List<UnusedIndex> unusedIndexesFromHost : potentiallyUnusedIndexesFromAllHosts) {
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
    static String getLastStatsResetDateLogMessage(
            @Nonnull final PgHost pgHost,
            @Nonnull final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHosts) {
        Objects.requireNonNull(statisticsMaintenanceForAllHosts, "statisticsMaintenanceForAllHosts cannot be null");
        final StatisticsMaintenanceOnHost statisticsMaintenance = statisticsMaintenanceForAllHosts.get(pgHost);
        if (statisticsMaintenance == null) {
            throw new NoSuchElementException("StatisticsMaintenanceOnHost object wasn't found for host " + pgHost);
        }

        final Optional<OffsetDateTime> statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        if (statsResetTimestamp.isPresent()) {
            final long daysBetween = ChronoUnit.DAYS.between(statsResetTimestamp.get(), OffsetDateTime.now(ClockHolder.clock()));
            return String.format("Last statistics reset on this host was %d days ago (%s)",
                    daysBetween, statsResetTimestamp.get());
        }
        return "Statistics have never been reset on this host";
    }
}
