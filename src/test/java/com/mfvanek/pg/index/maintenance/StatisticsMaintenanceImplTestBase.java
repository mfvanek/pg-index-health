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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    tryToFindAccountByClientId(schemaName);
                    final PgContext pgContext = PgContext.of(schemaName);
                    assertThat(getSeqScansForAccounts(pgContext), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
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
