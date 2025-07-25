/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.core.utils.ClockHolder;
import io.github.mfvanek.pg.model.context.PgContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsMaintenanceOnHostImplTest extends StatisticsAwareTestBase {

    private final StatisticsMaintenanceOnHost statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(getPgConnection());

    @Test
    void resetStatisticsOnEmptyDatabaseShouldExecuteCorrectly() {
        assertThat(statisticsMaintenance.resetStatistics())
            .isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final OffsetDateTime testStartTime = OffsetDateTime.now(ClockHolder.clock());
            tryToFindAccountByClientId(schemaName);
            final PgContext pgContext = PgContext.of(schemaName);
            assertThat(getSeqScansForAccounts(pgContext))
                .isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
            assertThat(statisticsMaintenance.resetStatistics())
                .isTrue();
            collectStatistics(schemaName);
            assertThat(getSeqScansForAccounts(pgContext))
                .isZero();

            assertThat(statisticsMaintenance.getLastStatsResetTimestamp())
                .isPresent()
                .get()
                .satisfies(t -> assertThat(t).isAfter(testStartTime));
        });
    }

    @Test
    void getHostShouldWork() {
        assertThat(statisticsMaintenance.getHost())
            .isEqualTo(getHost());
    }
}
