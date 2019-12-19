/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.HostAware;

public interface StatisticsMaintenance extends HostAware {

    /**
     * Reset all statistics counters for the current database on current host to zero.
     * For more information, see https://www.postgresql.org/docs/current/monitoring-stats.html
     */
    boolean resetStatistics();
}
