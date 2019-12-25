/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
