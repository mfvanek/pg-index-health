/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.common.maintenance.MaintenanceFactory;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see IndexesMaintenanceOnHost
 */
public class DatabaseManagementImpl implements DatabaseManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagementImpl.class);

    private final StatisticsMaintenanceOnHost statisticsMaintenanceForPrimary;
    private final Collection<StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHostsInCluster;

    public DatabaseManagementImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                  @Nonnull final MaintenanceFactory maintenanceFactory) {
        Objects.requireNonNull(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        this.statisticsMaintenanceForPrimary = maintenanceFactory.forStatistics(haPgConnection.getConnectionToPrimary());
        final Set<PgConnection> pgConnections = haPgConnection.getConnectionsToAllHostsInCluster();
        this.statisticsMaintenanceForAllHostsInCluster = maintenanceFactory.forStatistics(pgConnections);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetStatistics() {
        for (StatisticsMaintenanceOnHost statisticsMaintenance : statisticsMaintenanceForAllHostsInCluster) {
            doOnHost(statisticsMaintenance.getHost(), () -> {
                statisticsMaintenance.resetStatistics();
                return true;
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Optional<OffsetDateTime> getLastStatsResetTimestamp() {
        return doOnHost(statisticsMaintenanceForPrimary.getHost(), statisticsMaintenanceForPrimary::getLastStatsResetTimestamp);
    }

    private <T> T doOnHost(@Nonnull final PgHost host, Supplier<T> action) {
        LOGGER.debug("Going to execute on host {}", host.getName());
        return action.get();
    }
}
