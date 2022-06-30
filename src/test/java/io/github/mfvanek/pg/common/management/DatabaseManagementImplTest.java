/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.settings.ImportantParam;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.settings.maintenance.ConfigurationMaintenanceOnHostImpl;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHostImpl;
import io.github.mfvanek.pg.utils.ClockHolder;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseManagementImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final DatabaseManagement databaseManagement;

    DatabaseManagementImplTest() {
        super(POSTGRES.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(
                PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
        this.databaseManagement = new DatabaseManagementImpl(haPgConnection, StatisticsMaintenanceOnHostImpl::new, ConfigurationMaintenanceOnHostImpl::new);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final OffsetDateTime testStartTime = OffsetDateTime.now(ClockHolder.clock());
            tryToFindAccountByClientId(schemaName);
            assertThat(getSeqScansForAccounts(ctx)).isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
            databaseManagement.resetStatistics();
            waitForStatisticsCollector();
            assertThat(getSeqScansForAccounts(ctx)).isZero();

            assertThat(databaseManagement.getLastStatsResetTimestamp())
                    .isPresent()
                    .get()
                    .satisfies(t -> assertThat(t).isAfter(testStartTime));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnParamsWithDefaultValues(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final ServerSpecification specification = ServerSpecification.builder().withCpuCores(2).withMemoryAmount(2, MemoryUnit.GB).withSSD().build();
            assertThat(databaseManagement.getParamsWithDefaultValues(specification))
                    .hasSize(10)
                    .extracting(PgParam::getName)
                    .containsExactlyInAnyOrder("shared_buffers", "work_mem", "maintenance_work_mem", "random_page_cost", "log_min_duration_statement", "idle_in_transaction_session_timeout",
                            "statement_timeout", "effective_cache_size", "lock_timeout", "temp_file_limit");
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnParamsCurrentValues(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx ->
                assertThat(databaseManagement.getParamsCurrentValues())
                        .hasSizeGreaterThan(ImportantParam.values().length));
    }
}
