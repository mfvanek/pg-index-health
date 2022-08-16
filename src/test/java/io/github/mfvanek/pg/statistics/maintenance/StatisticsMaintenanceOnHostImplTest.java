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

import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.support.SharedDatabaseTestBase;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsMaintenanceOnHostImplTest extends SharedDatabaseTestBase {

    private final StatisticsMaintenanceOnHost statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(getPgConnection());

    @Test
    void resetStatisticsOnEmptyDatabaseShouldExecuteCorrectly() {
        assertThat(statisticsMaintenance.resetStatistics())
                .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final OffsetDateTime testStartTime = OffsetDateTime.now(ClockHolder.clock());
            tryToFindAccountByClientId(schemaName);
            final PgContext pgContext = PgContext.of(schemaName);
            assertThat(getSeqScansForAccounts(pgContext))
                    .isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
            assertThat(statisticsMaintenance.resetStatistics())
                    .isTrue();
            waitForStatisticsCollector();
            assertThat(getSeqScansForAccounts(pgContext))
                    .isZero();

            assertThat(statisticsMaintenance.getLastStatsResetTimestamp())
                    .isPresent()
                    .get()
                    .satisfies(t -> assertThat(t).isAfter(testStartTime));
        });
    }

    @Test
    void getHost() {
        final PgHost host = statisticsMaintenance.getHost();
        assertThat(host)
                .isNotNull()
                .extracting(PgHost::getName)
                .isEqualTo("primary");
    }
}
