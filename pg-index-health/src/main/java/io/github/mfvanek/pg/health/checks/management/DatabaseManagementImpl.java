/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see StatisticsMaintenanceOnHost
 */
public class DatabaseManagementImpl implements DatabaseManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagementImpl.class);

    private final HighAvailabilityPgConnection haPgConnection;
    private final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statistics;

    public DatabaseManagementImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                  @Nonnull final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection cannot be null");
        this.statisticsOnHostFactory = Objects.requireNonNull(statisticsOnHostFactory, "statisticsOnHostFactory cannot be null");
        this.statistics = new HashMap<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resetStatistics() {
        boolean result = true;
        for (final PgConnection pgConnection : haPgConnection.getConnectionsToAllHostsInCluster()) {
            LOGGER.debug("Going to execute on host {}", pgConnection.getHost().getName());
            final boolean resultOnHost = computeStatisticsForHostIfNeed(pgConnection).resetStatistics();
            result = result && resultOnHost;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Optional<OffsetDateTime> getLastStatsResetTimestamp() {
        return computeStatisticsForHostIfNeed(getPrimaryAndLog())
            .getLastStatsResetTimestamp();
    }

    @Nonnull
    private PgConnection getPrimaryAndLog() {
        final PgConnection connectionToPrimary = haPgConnection.getConnectionToPrimary();
        LOGGER.debug("Going to execute on primary host {}", connectionToPrimary.getHost().getName());
        return connectionToPrimary;
    }

    @Nonnull
    private StatisticsMaintenanceOnHost computeStatisticsForHostIfNeed(@Nonnull final PgConnection connectionToHost) {
        return statistics.computeIfAbsent(connectionToHost.getHost(), h -> statisticsOnHostFactory.apply(connectionToHost));
    }
}
