/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.statistics.StatisticsAware;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;

import javax.annotation.Nonnull;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface DatabaseManagement extends StatisticsAware {

    /**
     * Reset all statistics counters on all hosts in the cluster to zero.
     * <p>
     * It is safe running this method on your database.
     * It just reset counters without any impact on performance.
     *
     * @see StatisticsMaintenanceOnHost
     */
    @Override
    void resetStatistics();

    /**
     * Gets time at which database statistics were last reset on the primary host.
     *
     * @return {@code Optional} of null or time at which database statistics were last reset.
     */
    @Override
    @Nonnull
    Optional<OffsetDateTime> getLastStatsResetTimestamp();
}
