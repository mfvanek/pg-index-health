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

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.utils.ClockHolder;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsMaintenanceOnHostImplEmptyTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final StatisticsMaintenanceOnHost statisticsMaintenance;

    StatisticsMaintenanceOnHostImplEmptyTest() {
        super(POSTGRES.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase());
        this.statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @Test
    void getLastStatsResetTimestamp() {
        // Time of the last statistics reset is initialized to the system time during the first connection to the database.
        DatabasePopulator.collectStatistics(POSTGRES.getTestDatabase());
        waitForStatisticsCollector();
        final Optional<OffsetDateTime> statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        assertThat(statsResetTimestamp)
                .isNotNull()
                .isPresent()
                .get()
                .satisfies(t -> assertThat(t).isBeforeOrEqualTo(OffsetDateTime.now(ClockHolder.clock())));
    }
}
