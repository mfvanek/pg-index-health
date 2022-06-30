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
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.utils.ClockHolder;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class StatisticsMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final StatisticsMaintenanceOnHost statisticsMaintenance;

    StatisticsMaintenanceOnHostImplTest() {
        super(POSTGRES.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase());
        this.statisticsMaintenance = new StatisticsMaintenanceOnHostImpl(pgConnection);
    }

    @Test
    void resetStatisticsOnEmptyDatabaseShouldExecuteCorrectly() {
        assertThatCode(statisticsMaintenance::resetStatistics)
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final OffsetDateTime testStartTime = OffsetDateTime.now(ClockHolder.clock());
            tryToFindAccountByClientId(schemaName);
            final PgContext pgContext = PgContext.of(schemaName);
            assertThat(getSeqScansForAccounts(pgContext)).isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
            statisticsMaintenance.resetStatistics();
            waitForStatisticsCollector();
            assertThat(getSeqScansForAccounts(pgContext)).isZero();

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
