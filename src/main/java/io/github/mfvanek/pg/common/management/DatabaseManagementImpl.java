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
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see IndexesMaintenanceOnHost
 */
public class DatabaseManagementImpl implements DatabaseManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagementImpl.class);

    private final HighAvailabilityPgConnection haPgConnection;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statisticsMaintenanceForAllHostsInCluster;
    private final Map<PgHost, ConfigurationMaintenanceOnHost> configurationMaintenanceForAllHostsInCluster;

    public DatabaseManagementImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                  @Nonnull final MaintenanceFactory maintenanceFactory) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection");
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
        for (StatisticsMaintenanceOnHost statisticsMaintenance : statisticsMaintenanceForAllHostsInCluster.values()) {
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
        final PgHost primaryHost = getPrimaryHost();
        final StatisticsMaintenanceOnHost forPrimary = statisticsMaintenanceForAllHostsInCluster.get(primaryHost);
        return doOnHost(primaryHost, forPrimary::getLastStatsResetTimestamp);
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification) {
        final PgHost primaryHost = getPrimaryHost();
        final ConfigurationMaintenanceOnHost forPrimary = configurationMaintenanceForAllHostsInCluster.get(primaryHost);
        return doOnHost(primaryHost, () -> forPrimary.getParamsWithDefaultValues(specification));
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsCurrentValues() {
        final PgHost primaryHost = getPrimaryHost();
        final ConfigurationMaintenanceOnHost forPrimary = configurationMaintenanceForAllHostsInCluster.get(primaryHost);
        return doOnHost(primaryHost, forPrimary::getParamsCurrentValues);
    }

    private <T> T doOnHost(@Nonnull final PgHost host, Supplier<T> action) {
        LOGGER.debug("Going to execute on host {}", host.getName());
        return action.get();
    }

    @Nonnull
    private PgHost getPrimaryHost() {
        // Primary host may change its location within the cluster due to failover or switchover.
        // So we need to ensure where the primary is.
        return haPgConnection.getConnectionToPrimary().getHost();
    }
}
