/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;

public class MaintenanceFactoryImpl implements MaintenanceFactory {

    @Override
    @Nonnull
    public IndexMaintenance forIndex(@Nonnull final PgConnection pgConnection) {
        return new IndexMaintenanceImpl(pgConnection);
    }

    @Nonnull
    @Override
    public StatisticsMaintenance forStatistics(@Nonnull PgConnection pgConnection) {
        return new StatisticsMaintenanceImpl(pgConnection);
    }
}
