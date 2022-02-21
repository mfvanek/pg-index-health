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

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.index.maintenance.IndexMaintenanceOnHostImpl;
import io.github.mfvanek.pg.index.maintenance.IndexesMaintenanceOnHost;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHost;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHostImpl;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHost;
import io.github.mfvanek.pg.table.maintenance.TablesMaintenanceOnHostImpl;

import javax.annotation.Nonnull;

public class MaintenanceFactoryImpl implements MaintenanceFactory {

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public IndexesMaintenanceOnHost forIndexes(@Nonnull final PgConnection pgConnection) {
        return new IndexMaintenanceOnHostImpl(pgConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public TablesMaintenanceOnHost forTables(@Nonnull PgConnection pgConnection) {
        return new TablesMaintenanceOnHostImpl(pgConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public StatisticsMaintenanceOnHost forStatistics(@Nonnull PgConnection pgConnection) {
        return new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @Override
    @Nonnull
    public ConfigurationMaintenanceOnHost forConfiguration(@Nonnull PgConnection pgConnection) {
        return new ConfigurationMaintenanceOnHostImpl(pgConnection);
    }
}
