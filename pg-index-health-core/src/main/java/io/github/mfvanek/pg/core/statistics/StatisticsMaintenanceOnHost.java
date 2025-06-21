/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.connection.host.HostAware;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * An entry point for managing statistics on the specified host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 */
public interface StatisticsMaintenanceOnHost extends StatisticsAware, HostAware {

    /**
     * Resets all statistics counters for the current database on current host to zero.
     * <p>
     * Note: superuser privileges are required.
     *
     * @return true if the operation is successful
     * @see <a href="https://www.postgresql.org/docs/current/monitoring-stats.html">Monitoring Database Activity</a>
     */
    @Override
    boolean resetStatistics();

    /**
     * Retrieves the time at which database statistics were last reset on current host.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    @Override
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
