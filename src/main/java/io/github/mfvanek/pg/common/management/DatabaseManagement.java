/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
    boolean resetStatistics();
}
