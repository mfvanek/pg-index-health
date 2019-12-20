/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;

public class MaintenanceFactoryImpl implements MaintenanceFactory {

    @Override
    @Nonnull
    public IndexMaintenance forIndex(@Nonnull final PgConnection pgConnection,
                                     @Nonnull final PgContext pgContext) {
        return new IndexMaintenanceImpl(pgConnection, pgContext);
    }

    @Nonnull
    @Override
    public StatisticsMaintenance forStatistics(@Nonnull PgConnection pgConnection) {
        return new StatisticsMaintenanceImpl(pgConnection);
    }
}
