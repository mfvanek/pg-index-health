/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HealthLoggerTest extends HealthLoggerTestBase {

    private static final LocalDateTime BEFORE_MILLENNIUM = LocalDateTime.of(1999, Month.DECEMBER, 31, 23, 59, 59);
    private static final Clock FIXED_CLOCK = Clock.fixed(BEFORE_MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private static Clock originalClock;

    private final HealthLogger logger = new KeyValueFileHealthLogger(
            getConnectionCredentials(),
            new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), new PrimaryHostDeterminerImpl()),
            DatabaseChecks::new);

    @BeforeAll
    static void setUp() {
        originalClock = ClockHolder.setClock(FIXED_CLOCK);
    }

    @AfterAll
    static void tearDown() {
        if (originalClock != null) {
            ClockHolder.setClock(originalClock);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logAll(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences()
                        .withData()
                        .withInvalidIndex()
                        .withNullValuesInIndex()
                        .withTableWithoutPrimaryKey()
                        .withDuplicatedIndex()
                        .withNonSuitableIndex()
                        .withJsonType()
                        .withSerialType()
                        .withFunctions(),
                ctx -> {
                    collectStatistics(schemaName);
                    assertThat(logger.logAll(Exclusions.empty(), ctx))
                            .hasSameSizeAs(Diagnostic.values())
                            .containsExactlyInAnyOrder(
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tinvalid_indexes\t1",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tduplicated_indexes\t2",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tforeign_keys_without_index\t1",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_without_primary_key\t1",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tindexes_with_null_values\t1",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tindexes_with_bloat\t11",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_bloat\t2",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tintersected_indexes\t5",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tunused_indexes\t7",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_missing_indexes\t0",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_without_description\t4",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_without_description\t17",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_with_json_type\t1",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_with_serial_types\t2",
                                    "1999-12-31T23:59:59Z\tdb_indexes_health\tfunctions_without_description\t2");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logTablesWithMissingIndexes(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);

            assertThat(logger.logAll(Exclusions.empty(), ctx))
                    .hasSameSizeAs(Diagnostic.values())
                    .filteredOn(ofKey(SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES))
                    .hasSize(1)
                    .containsExactly("1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_missing_indexes\t1");
        });
    }

    @Test
    void logAllWithDefaultSchema() {
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertThat(logs)
                .hasSameSizeAs(Diagnostic.values());
        for (final SimpleLoggingKey key : SimpleLoggingKey.values()) {
            assertThat(logs)
                    .filteredOn(ofKey(key))
                    .hasSize(1)
                    .containsExactly("1999-12-31T23:59:59Z\tdb_indexes_health\t" + key.getSubKeyName() + "\t0");
        }
    }

    @Test
    void completenessTest() {
        assertThat(logger.logAll(Exclusions.empty()))
                .as("All diagnostics must be logged")
                .hasSameSizeAs(Diagnostic.values());
    }
}
