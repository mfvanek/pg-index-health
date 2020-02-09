/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.HostAware;

/**
 * An entry point for managing statistics on the specified host.
 *
 * @author Ivan Vakhrushev
 * @see HostAware
 */
public interface StatisticsMaintenance extends HostAware {

    /**
     * Resets all statistics counters for the current database on current host to zero.
     * For more information, see https://www.postgresql.org/docs/current/monitoring-stats.html
     *
     * @return true if has been called successfully
     */
    boolean resetStatistics();
}
