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

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.statistics.StatisticsAware;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.annotation.Nonnull;

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
     * @see <a href="https://www.postgresql.org/docs/current/monitoring-stats.html">Monitoring Database Activity</a>
     * @return true if the operation is successful
     */
    @Override
    boolean resetStatistics();

    /**
     * Gets time at which database statistics were last reset on current host.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    @Override
    @Nonnull
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
