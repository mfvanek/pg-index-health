/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.settings.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.model.settings.PgParam;
import io.github.mfvanek.pg.model.settings.ServerSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Implementation of {@code DatabaseManagement}.
 *
 * @author Ivan Vakhrushev
 * @see StatisticsMaintenanceOnHost
 * @see ConfigurationMaintenanceOnHost
 */
public class DatabaseManagementImpl implements DatabaseManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManagementImpl.class);

    private final HighAvailabilityPgConnection haPgConnection;
    private final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory;
    private final Function<PgConnection, ConfigurationMaintenanceOnHost> configurationOnHostFactory;
    private final Map<PgHost, StatisticsMaintenanceOnHost> statistics;
    private final Map<PgHost, ConfigurationMaintenanceOnHost> configuration;

    public DatabaseManagementImpl(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                  @Nonnull final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory,
                                  @Nonnull final Function<PgConnection, ConfigurationMaintenanceOnHost> configurationOnHostFactory) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection cannot be null");
        this.statisticsOnHostFactory = Objects.requireNonNull(statisticsOnHostFactory, "statisticsOnHostFactory cannot be null");
        this.configurationOnHostFactory = Objects.requireNonNull(configurationOnHostFactory, "configurationOnHostFactory cannot be null");
        this.statistics = new HashMap<>();
        this.configuration = new HashMap<>();
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
        return computeStatisticsForHostIfNeed(getPrimaryAngLog())
            .getLastStatsResetTimestamp();
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsWithDefaultValues(@Nonnull final ServerSpecification specification) {
        return computeConfigurationForHostIfNeed(getPrimaryAngLog())
            .getParamsWithDefaultValues(specification);
    }

    @Override
    @Nonnull
    public Set<PgParam> getParamsCurrentValues() {
        return computeConfigurationForHostIfNeed(getPrimaryAngLog())
            .getParamsCurrentValues();
    }

    @Nonnull
    private PgConnection getPrimaryAngLog() {
        final PgConnection connectionToPrimary = haPgConnection.getConnectionToPrimary();
        LOGGER.debug("Going to execute on primary host {}", connectionToPrimary.getHost().getName());
        return connectionToPrimary;
    }

    @Nonnull
    private StatisticsMaintenanceOnHost computeStatisticsForHostIfNeed(@Nonnull final PgConnection connectionToHost) {
        return statistics.computeIfAbsent(connectionToHost.getHost(), h -> statisticsOnHostFactory.apply(connectionToHost));
    }

    @Nonnull
    private ConfigurationMaintenanceOnHost computeConfigurationForHostIfNeed(@Nonnull final PgConnection connectionToHost) {
        return configuration.computeIfAbsent(connectionToHost.getHost(), h -> configurationOnHostFactory.apply(connectionToHost));
    }
}
