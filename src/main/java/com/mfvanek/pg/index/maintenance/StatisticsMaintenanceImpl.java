/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PgHost;
import com.mfvanek.pg.utils.QueryExecutor;

import javax.annotation.Nonnull;
import java.util.Objects;

public class StatisticsMaintenanceImpl implements StatisticsMaintenance {

    private final PgConnection pgConnection;

    public StatisticsMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Override
    public boolean resetStatistics() {
        final var result = QueryExecutor.executeQuery(pgConnection, "select pg_stat_reset()", rs -> true);
        return result.size() == 1;
    }

    @Nonnull
    @Override
    public PgHost getHost() {
        return pgConnection.getHost();
    }
}
