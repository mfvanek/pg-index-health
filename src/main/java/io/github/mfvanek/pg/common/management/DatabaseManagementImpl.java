/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see StatisticsMaintenanceOnHost
 * @see ConfigurationMaintenanceOnHost
 */
public class DatabaseManagementImpl extends AbstractManagement implements DatabaseManagement {

    private final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHostsInCluster;
    private final Map<PgHost, ConfigurationMaintenanceOnHost> configurationMaintenanceForAllHostsInCluster;

    public DatabaseManagementImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                  @Nonnull final MaintenanceFactory maintenanceFactory) {
        super(haPgConnection);
        Objects.requireNonNull(maintenanceFactory);
        final Set<PgConnection> pgConnections = haPgConnection.getConnectionsToAllHostsInCluster();
        this.statisticsMaintenanceForAllHostsInCluster = maintenanceFactory.forStatistics(pgConnections);
        this.configurationMaintenanceForAllHostsInCluster = maintenanceFactory.forConfiguration(pgConnections);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetStatistics() {
        for (final StatisticsMaintenanceOnHost statisticsMaintenance : statisticsMaintenanceForAllHostsInCluster.values()) {
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
        return doOnPrimary(statisticsMaintenanceForAllHostsInCluster, StatisticsMaintenanceOnHost::getLastStatsResetTimestamp);
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsWithDefaultValues(@Nonnull final ServerSpecification specification) {
        return doOnPrimary(configurationMaintenanceForAllHostsInCluster, ConfigurationMaintenanceOnHost::getParamsWithDefaultValues, specification);
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsCurrentValues() {
        return doOnPrimary(configurationMaintenanceForAllHostsInCluster, ConfigurationMaintenanceOnHost::getParamsCurrentValues);
    }
}
