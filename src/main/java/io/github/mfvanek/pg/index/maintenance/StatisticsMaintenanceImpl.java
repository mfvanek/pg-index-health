/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.utils.QueryExecutor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class StatisticsMaintenanceImpl implements StatisticsMaintenance {

    private final PgConnection pgConnection;

    public StatisticsMaintenanceImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection);
    }

    @Override
    public boolean resetStatistics() {
        final List<Boolean> result = QueryExecutor.executeQuery(pgConnection, "select pg_stat_reset()", rs -> true);
        return result.size() == 1;
    }

    @Nonnull
    @Override
    public PgHost getHost() {
        return pgConnection.getHost();
    }
}
