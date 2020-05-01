/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatisticsMaintenanceOnHostImplEmptyTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final StatisticsMaintenanceOnHost statisticsMaintenance;

    StatisticsMaintenanceOnHostImplEmptyTest() {
        super(embeddedPostgres.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        this.statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @RepeatedTest(5)
    void getLastStatsResetTimestamp() {
        // Time of the last statistics reset is initialized to the system time during the first connection to the database.
        final OffsetDateTime statsResetTimestamp = statisticsMaintenance.getLastStatsResetTimestamp();
        assertNotNull(statsResetTimestamp);
        assertThat(statsResetTimestamp, lessThanOrEqualTo(OffsetDateTime.now()));
    }
}
