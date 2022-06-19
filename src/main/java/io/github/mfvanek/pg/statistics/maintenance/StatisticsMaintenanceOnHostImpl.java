/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.statistics.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.utils.QueryExecutors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

public class StatisticsMaintenanceOnHostImpl implements StatisticsMaintenanceOnHost {

    /**
     * A connection to a specific host in the cluster.
     */
    private final PgConnection pgConnection;

    public StatisticsMaintenanceOnHostImpl(@Nonnull final PgConnection pgConnection) {
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetStatistics() {
        QueryExecutors.executeQuery(pgConnection, "select pg_stat_reset()", rs -> true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Optional<OffsetDateTime> getLastStatsResetTimestamp() {
        final String query = "select stats_reset from pg_stat_database where datname = current_database()";
        final List<OffsetDateTime> statsResetTimes = QueryExecutors.executeQuery(pgConnection, query,
                rs -> rs.getObject(1, OffsetDateTime.class));
        return Optional.ofNullable(statsResetTimes.get(0));
    }
}
