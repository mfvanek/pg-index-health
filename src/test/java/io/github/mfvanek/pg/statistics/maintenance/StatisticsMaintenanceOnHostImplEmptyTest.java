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

import io.github.mfvanek.pg.utils.ClockHolder;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsMaintenanceOnHostImplEmptyTest extends SharedDatabaseTestBase {

    private final StatisticsMaintenanceOnHost statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(getPgConnection());

    @Test
    void getLastStatsResetTimestamp() {
        // Time of the last statistics reset is initialized to the system time during the first connection to the database.
        DatabasePopulator.collectStatistics(getDataSource());
        waitForStatisticsCollector();

        assertThat(statisticsMaintenance.getLastStatsResetTimestamp())
                .isPresent()
                .get()
                .satisfies(t -> assertThat(t).isBeforeOrEqualTo(OffsetDateTime.now(ClockHolder.clock())));
    }
}
