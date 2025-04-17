/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.utils.ClockHolder;
import io.github.mfvanek.pg.model.context.PgContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

class HealthLoggerTest extends HealthLoggerTestBase {

    private static final LocalDateTime BEFORE_MILLENNIUM = LocalDateTime.of(1999, Month.DECEMBER, 31, 23, 59, 59);
    private static final Clock FIXED_CLOCK = Clock.fixed(BEFORE_MILLENNIUM.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    private static Clock originalClock;

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

    @Nonnull
    @Override
    protected HealthLogger getHealthLogger() {
        return new KeyValueFileHealthLogger(getConnectionCredentials(), getConnectionFactory(), DatabaseChecksOnCluster::new);
    }

    @Nonnull
    @Override
    protected String[] getExpectedValue() {
        return new String[]{
            "1999-12-31T23:59:59Z\tdb_indexes_health\tinvalid_indexes\t1",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tduplicated_indexes\t2",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tforeign_keys_without_index\t8",
            "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_without_primary_key\t4",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tindexes_with_null_values\t1",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tbloated_indexes\t19",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tbloated_tables\t4",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tintersected_indexes\t11",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tunused_indexes\t12",
            "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_missing_indexes\t0",
            "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_without_description\t14",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_without_description\t36",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_with_json_type\t1",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_with_serial_types\t3",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tfunctions_without_description\t3",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tindexes_with_boolean\t1",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tnot_valid_constraints\t3",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tbtree_indexes_on_array_columns\t2",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tsequence_overflow\t3",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tprimary_keys_with_serial_types\t2",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tduplicated_foreign_keys\t3",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tintersected_foreign_keys\t1",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tpossible_object_name_overflow\t2",
            "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_not_linked_to_others\t5",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tforeign_keys_with_unmatched_column_type\t2",
            "1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_zero_or_one_column\t6",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tobjects_not_following_naming_convention\t14",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tcolumns_not_following_naming_convention\t4",
            "1999-12-31T23:59:59Z\tdb_indexes_health\tprimary_keys_with_varchar\t3"
        };
    }

    @Nonnull
    @Override
    protected String getExpectedValueForDefaultSchema(@Nonnull final Diagnostic diagnostic) {
        final LoggingKey key = SimpleLoggingKeyAdapter.of(diagnostic);
        return "1999-12-31T23:59:59Z\tdb_indexes_health\t" + key.getSubKeyName() + "\t0";
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logTablesWithMissingIndexes(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);

            assertThat(getHealthLogger().logAll(Exclusions.empty(), ctx))
                .hasSameSizeAs(Diagnostic.values())
                .filteredOn(ofKey(Diagnostic.TABLES_WITH_MISSING_INDEXES))
                .hasSize(1)
                .containsExactly("1999-12-31T23:59:59Z\tdb_indexes_health\ttables_with_missing_indexes\t1");
        });
    }
}
