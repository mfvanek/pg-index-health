/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;

public interface MaintenanceFactory {

    @Nonnull
    IndexMaintenance forIndex(@Nonnull PgConnection pgConnection, @Nonnull PgContext pgContext);

    @Nonnull
    StatisticsMaintenance forStatistics(@Nonnull PgConnection pgConnection);
}
