/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.management;

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.core.utils.ClockHolder;
import io.github.mfvanek.pg.model.context.PgContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagementImplTest extends StatisticsAwareTestBase {

    private final DatabaseManagement databaseManagement = new DatabaseManagementImpl(getHaPgConnection(), StatisticsMaintenanceOnHostImpl::new);

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldResetCounters(final String schemaName) {
        try (LogsCaptor ignored = new LogsCaptor(DatabaseManagementImpl.class, Level.FINEST)) {
            executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
                final OffsetDateTime testStartTime = OffsetDateTime.now(ClockHolder.clock());
                tryToFindAccountByClientId(schemaName);
                assertThat(getSeqScansForAccounts(ctx))
                    .isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
                assertThat(databaseManagement.resetStatistics())
                    .isTrue();
                collectStatistics(schemaName);
                assertThat(getSeqScansForAccounts(ctx))
                    .isZero();

                assertThat(databaseManagement.getLastStatsResetTimestamp())
                    .isPresent()
                    .get()
                    .satisfies(t -> assertThat(t).isAfter(testStartTime));
            });
        }
    }
}
