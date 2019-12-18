/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

abstract class StatisticsMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private final StatisticsMaintenance statisticsMaintenance;

    StatisticsMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(dataSource);
        this.statisticsMaintenance = new StatisticsMaintenanceImpl(pgConnection);
    }

    @Test
    void resetStatisticsOnEmptyDatabaseShouldExecuteCorrectly() {
        assertTrue(statisticsMaintenance.resetStatistics());
    }

    @Test
    void shouldResetCounters() {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.tryToFindAccountByClientId(101);
                },
                () -> {
                    assertThat(getSeqScansForAccounts(), greaterThanOrEqualTo(101L));
                    assertTrue(statisticsMaintenance.resetStatistics());
                    waitForStatisticsCollector();
                    assertEquals(0L, getSeqScansForAccounts());
                });
    }

    @Test
    void getHost() {
        final var host = statisticsMaintenance.getHost();
        assertNotNull(host);
        assertEquals("master", host.getName());
    }

    private long getSeqScansForAccounts() {
        final String sqlQuery =
                "select psat.relname::text as table_name, coalesce(psat.seq_scan, 0) as seq_scan\n" +
                        "from pg_catalog.pg_stat_all_tables psat\n" +
                        "where psat.schemaname = 'public'::text and psat.relname = 'accounts'::text;";
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                resultSet.next();
                return resultSet.getLong(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForStatisticsCollector() {
        try {
            Thread.sleep(500L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail();
        }
    }
}
