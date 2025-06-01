/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * A set of methods to manage statistics.
 *
 * @author Ivan Vakhrushev
 */
public interface StatisticsAware {

    /**
     * Resets all statistics counters for the current database to zero.
     * <p>
     * Note: superuser privileges are required.
     *
     * @return true if the operation is successful
     * @see <a href="https://www.postgresql.org/docs/current/monitoring-stats.html">Monitoring Database Activity</a>
     */
    boolean resetStatistics();

    /**
     * Retrieves the time at which database statistics were last reset.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
