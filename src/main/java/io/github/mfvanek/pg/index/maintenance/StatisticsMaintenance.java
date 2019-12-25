/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.HostAware;

public interface StatisticsMaintenance extends HostAware {

    /**
     * Reset all statistics counters for the current database on current host to zero.
     * For more information, see https://www.postgresql.org/docs/current/monitoring-stats.html
     */
    boolean resetStatistics();
}
