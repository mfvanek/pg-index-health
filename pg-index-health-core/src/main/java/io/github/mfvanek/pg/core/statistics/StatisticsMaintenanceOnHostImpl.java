/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.utils.QueryExecutors;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of the StatisticsMaintenanceOnHost interface, responsible for
 * handling database statistics on a specific PostgreSQL host.
 */
public class StatisticsMaintenanceOnHostImpl implements StatisticsMaintenanceOnHost {

    /**
     * A connection to a specific host in the cluster.
     */
    private final PgConnection pgConnection;
    private final StatisticsQueryExecutor queryExecutor;

    StatisticsMaintenanceOnHostImpl(final PgConnection pgConnection,
                                    final StatisticsQueryExecutor queryExecutor) {
        this.pgConnection = Objects.requireNonNull(pgConnection, "pgConnection cannot be null");
        this.queryExecutor = Objects.requireNonNull(queryExecutor, "queryExecutor cannot be null");
    }

    /**
     * Constructs a new instance of the {@code StatisticsMaintenanceOnHostImpl} class, leveraging the provided
     * {@link PgConnection} to manage database statistics on a specific PostgreSQL host. Uses a default query
     * executor implementation for database operations.
     *
     * @param pgConnection the connection to a specific PostgreSQL host; must not be null
     */
    public StatisticsMaintenanceOnHostImpl(final PgConnection pgConnection) {
        this(pgConnection, QueryExecutors::executeQuery);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgHost getHost() {
        return pgConnection.getHost();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean resetStatistics() {
        final List<Boolean> result = queryExecutor.executeQuery(pgConnection, "select pg_stat_reset()", rs -> Boolean.TRUE);
        return !result.isEmpty() && result.get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<OffsetDateTime> getLastStatsResetTimestamp() {
        final String query = "select stats_reset from pg_stat_database where datname = current_database()";
        final List<OffsetDateTime> statsResetTimes = queryExecutor.executeQuery(pgConnection, query,
            rs -> rs.getObject(1, OffsetDateTime.class));
        return Optional.ofNullable(statsResetTimes.isEmpty() ? null : statsResetTimes.get(0));
    }
}
