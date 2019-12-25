/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;

public interface MaintenanceFactory {

    @Nonnull
    IndexMaintenance forIndex(@Nonnull PgConnection pgConnection);

    @Nonnull
    StatisticsMaintenance forStatistics(@Nonnull PgConnection pgConnection);
}
