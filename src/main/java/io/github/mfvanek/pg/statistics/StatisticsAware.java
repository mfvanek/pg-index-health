/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.statistics;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * A set of methods to manage statistics.
 *
 * @author Ivan Vakhrushev
 */
public interface StatisticsAware {

    /**
     * Resets all statistics counters for the current database to zero.
     * For more information, see https://www.postgresql.org/docs/current/monitoring-stats.html
     */
    void resetStatistics();

    /**
     * Gets time at which database statistics were last reset.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    @Nonnull
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
