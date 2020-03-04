/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.PostgresExtensionFactory;
import io.github.mfvanek.pg.PostgresDbExtension;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public final class StatisticsMaintenanceImplTest extends DatabaseAwareTestBase {
    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres =
            PostgresExtensionFactory.database();

    private final StatisticsMaintenance statisticsMaintenance;

    StatisticsMaintenanceImplTest() {
        super(embeddedPostgres.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
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
        final PgHost host = statisticsMaintenance.getHost();
        assertNotNull(host);
        assertEquals("master", host.getName());
    }
}
