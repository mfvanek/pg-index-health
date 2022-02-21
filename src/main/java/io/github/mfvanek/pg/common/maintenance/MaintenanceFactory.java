/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Factory for creating maintenance objects with a given {@link PgConnection}.
 *
 * @author Ivan Vakhrushev
 * @see IndexesMaintenanceOnHost
 * @see TablesMaintenanceOnHost
 * @see StatisticsMaintenanceOnHost
 * @see ConfigurationMaintenanceOnHost
 * @see PgConnection
 */
public interface MaintenanceFactory {

    @Nonnull
    IndexesMaintenanceOnHost forIndexes(@Nonnull PgConnection pgConnection);

    @Nonnull
    default Collection<IndexesMaintenanceOnHost> forIndexes(@Nonnull final Collection<PgConnection> pgConnections) {
        return Collections.unmodifiableList(
                pgConnections.stream()
                        .map(this::forIndexes)
                        .collect(Collectors.toList())
        );
    }

    @Nonnull
    TablesMaintenanceOnHost forTables(@Nonnull PgConnection pgConnection);

    @Nonnull
    default Collection<TablesMaintenanceOnHost> forTables(@Nonnull final Collection<PgConnection> pgConnections) {
        return Collections.unmodifiableList(
                pgConnections.stream()
                        .map(this::forTables)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Creates statistics maintenance object.
     *
     * @param pgConnection given connection to the host
     * @return {@code StatisticsMaintenanceOnHost} object
     */
    @Nonnull
    StatisticsMaintenanceOnHost forStatistics(@Nonnull PgConnection pgConnection);

    @Nonnull
    default Map<PgHost, StatisticsMaintenanceOnHost> forStatistics(@Nonnull final Collection<PgConnection> pgConnections) {
        return Collections.unmodifiableMap(
                pgConnections.stream()
                        .map(this::forStatistics)
                        .collect(Collectors.toMap(HostAware::getHost, Function.identity()))
        );
    }

    @Nonnull
    ConfigurationMaintenanceOnHost forConfiguration(@Nonnull PgConnection pgConnection);

    @Nonnull
    default Map<PgHost, ConfigurationMaintenanceOnHost> forConfiguration(@Nonnull final Collection<PgConnection> pgConnections) {
        return Collections.unmodifiableMap(
                pgConnections.stream()
                        .map(this::forConfiguration)
                        .collect(Collectors.toMap(HostAware::getHost, Function.identity()))
        );
    }
}
