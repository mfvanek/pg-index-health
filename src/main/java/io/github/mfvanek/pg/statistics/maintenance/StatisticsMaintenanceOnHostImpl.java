/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.statistics.maintenance;

import io.github.mfvanek.pg.common.maintenance.AbstractMaintenance;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.utils.QueryExecutor;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.List;

public class StatisticsMaintenanceOnHostImpl extends AbstractMaintenance implements StatisticsMaintenanceOnHost {

    public StatisticsMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        super(pgConnection);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resetStatistics() {
        final List<Boolean> result = QueryExecutor.executeQuery(pgConnection, "select pg_stat_reset()", rs -> true);
        return result.size() == 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public OffsetDateTime getLastStatsResetTimestamp() {
        final String query = "select stats_reset from pg_stat_database where datname = current_database()";
        final List<OffsetDateTime> statsResetTimes = QueryExecutor.executeQuery(pgConnection, query,
                rs -> rs.getObject(1, OffsetDateTime.class));
        return statsResetTimes.get(0);
    }
}
