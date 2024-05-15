/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.settings.ImportantParam;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHostImpl;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagementImplTest extends StatisticsAwareTestBase {

    private final DatabaseManagement databaseManagement = new DatabaseManagementImpl(getHaPgConnection(),
        StatisticsMaintenanceOnHostImpl::new, ConfigurationMaintenanceOnHostImpl::new);

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldResetCounters(final String schemaName) {
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

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldReturnParamsWithDefaultValues(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final ServerSpecification specification = ServerSpecification.builder().withCpuCores(2).withMemoryAmount(2, MemoryUnit.GB).withSSD().build();
            assertThat(databaseManagement.getParamsWithDefaultValues(specification))
                .hasSize(5)
                .extracting(PgParam::getName)
                .containsExactlyInAnyOrder("log_min_duration_statement", "idle_in_transaction_session_timeout", "statement_timeout", "effective_cache_size", "temp_file_limit");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldReturnParamsCurrentValues(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx ->
            assertThat(databaseManagement.getParamsCurrentValues())
                .hasSizeGreaterThan(ImportantParam.values().length)
                .isUnmodifiable());
    }
}
