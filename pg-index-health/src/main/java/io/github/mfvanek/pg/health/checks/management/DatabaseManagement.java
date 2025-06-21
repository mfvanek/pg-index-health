/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.management;

import io.github.mfvanek.pg.core.statistics.StatisticsAware;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface DatabaseManagement extends StatisticsAware {

    /**
     * Reset all statistics counters on all hosts in the cluster to zero.
     * <p>
     * It is safe running this method on your database.
     * It just reset counters without any impact on performance.
     *
     * @return true if the operation is successful
     * @see StatisticsMaintenanceOnHost
     */
    @Override
    boolean resetStatistics();

    /**
     * Retrieves the time at which database statistics were last reset on the primary host.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    @Override
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
