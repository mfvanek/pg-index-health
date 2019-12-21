/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.model.PgContext;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class StatisticsMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private final StatisticsMaintenance statisticsMaintenance;

    StatisticsMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(dataSource);
        this.statisticsMaintenance = new StatisticsMaintenanceImpl(pgConnection);
    }

    @Test
    void resetStatisticsOnEmptyDatabaseShouldExecuteCorrectly() {
        statisticsMaintenance.resetStatistics();
    }

    @Test
    void shouldResetCounters() {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.withReferences().withData().populate();
                    databasePopulator.tryToFindAccountByClientId(101);
                },
                () -> {
                    final PgContext pgContext = PgContext.ofPublic();
                    assertThat(getSeqScansForAccounts(pgContext), greaterThanOrEqualTo(101L));
                    statisticsMaintenance.resetStatistics();
                    waitForStatisticsCollector();
                    assertEquals(0L, getSeqScansForAccounts(pgContext));
                });
    }

    @Test
    void getHost() {
        final var host = statisticsMaintenance.getHost();
        assertNotNull(host);
        assertEquals("master", host.getName());
    }
}
