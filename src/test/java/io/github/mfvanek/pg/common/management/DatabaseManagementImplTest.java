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

import io.github.mfvanek.pg.common.maintenance.MaintenanceFactoryImpl;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.settings.ImportantParam;
import io.github.mfvanek.pg.settings.PgParam;
import io.github.mfvanek.pg.settings.ServerSpecification;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseManagementImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final DatabaseManagement databaseManagement;

    DatabaseManagementImplTest() {
        super(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(
                PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase()));
        this.databaseManagement = new DatabaseManagementImpl(haPgConnection, new MaintenanceFactoryImpl());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final OffsetDateTime testStartTime = OffsetDateTime.now();
                    tryToFindAccountByClientId(schemaName);
                    assertThat(getSeqScansForAccounts(ctx), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
                    databaseManagement.resetStatistics();
                    waitForStatisticsCollector();
                    assertEquals(0L, getSeqScansForAccounts(ctx));

                    final Optional<OffsetDateTime> statsResetTimestamp = databaseManagement.getLastStatsResetTimestamp();
                    assertNotNull(statsResetTimestamp);
                    assertTrue(statsResetTimestamp.isPresent());
                    assertThat(statsResetTimestamp.get(), greaterThan(testStartTime));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnParamsWithDefaultValues(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final ServerSpecification specification = ServerSpecification.builder()
                            .withCpuCores(2)
                            .withMemoryAmount(2, MemoryUnit.GB)
                            .withSSD()
                            .build();
                    final Set<PgParam> paramsWithDefaultValues = databaseManagement.getParamsWithDefaultValues(specification);
                    assertNotNull(paramsWithDefaultValues);
                    assertThat(paramsWithDefaultValues, hasSize(10));
                    assertThat(paramsWithDefaultValues.stream()
                            .map(PgParam::getName)
                            .collect(toList()), containsInAnyOrder(
                            "shared_buffers",
                            "work_mem",
                            "maintenance_work_mem",
                            "random_page_cost",
                            "log_min_duration_statement",
                            "idle_in_transaction_session_timeout",
                            "statement_timeout",
                            "effective_cache_size",
                            "lock_timeout",
                            "temp_file_limit")
                    );
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnParamsCurrentValues(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final Set<PgParam> paramsCurrentValues = databaseManagement.getParamsCurrentValues();
                    assertNotNull(paramsCurrentValues);
                    assertThat(paramsCurrentValues.size(), greaterThan(ImportantParam.values().length));
                });
    }
}
