/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see StatisticsMaintenanceOnHost
 */
public class DatabaseManagementImpl implements DatabaseManagement {

    private static final Logger LOGGER = Logger.getLogger(DatabaseManagementImpl.class.getName());

    private final HighAvailabilityPgConnection haPgConnection;
    private final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statistics;

    /**
     * Constructs an instance of {@code DatabaseManagementImpl}.
     *
     * @param haPgConnection          the high-availability connection to the PostgreSQL cluster; must not be null
     * @param statisticsOnHostFactory a factory function for creating {@code StatisticsMaintenanceOnHost} instances given a {@code PgConnection}; must not be null
     * @throws NullPointerException if {@code haPgConnection} or {@code statisticsOnHostFactory} is null
     */
    public DatabaseManagementImpl(final HighAvailabilityPgConnection haPgConnection,
                                  final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory) {
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
            LOGGER.fine(() -> "Going to execute on host " + pgConnection.getHost().getName());
            final boolean resultOnHost = computeStatisticsForHostIfNeed(pgConnection).resetStatistics();
            result = result && resultOnHost;
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OffsetDateTime> getLastStatsResetTimestamp() {
        return computeStatisticsForHostIfNeed(getPrimaryAndLog())
            .getLastStatsResetTimestamp();
    }

    private PgConnection getPrimaryAndLog() {
        final PgConnection connectionToPrimary = haPgConnection.getConnectionToPrimary();
        LOGGER.fine(() -> "Going to execute on primary host " + connectionToPrimary.getHost().getName());
        return connectionToPrimary;
    }

    private StatisticsMaintenanceOnHost computeStatisticsForHostIfNeed(final PgConnection connectionToHost) {
        return statistics.computeIfAbsent(connectionToHost.getHost(), h -> statisticsOnHostFactory.apply(connectionToHost));
    }
}
