/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.PrimaryHostDeterminerImpl;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactoryImpl;
import io.github.mfvanek.pg.connection.factory.PgConnectionFactoryImpl;
import io.github.mfvanek.pg.core.checks.common.CheckNameAware;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseConfigurer;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.health.checks.cluster.StandardChecksOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class StandardHealthLoggerTest extends StatisticsAwareTestBase {

    private static final DatabaseConfigurer CONFIGURER = dbp -> dbp.withReferences()
        .withData()
        .withInvalidIndex()
        .withNullValuesInIndex()
        .withBooleanValuesInIndex()
        .withTableWithoutPrimaryKey()
        .withDuplicatedIndex()
        .withNonSuitableIndex()
        .withJsonType()
        .withSerialType()
        .withFunctions()
        .withNotValidConstraints()
        .withBtreeIndexesOnArrayColumn()
        .withSequenceOverflow()
        .withDuplicatedForeignKeys()
        .withIntersectedForeignKeys()
        .withMaterializedView()
        .withIdentityPrimaryKey()
        .withForeignKeyOnNullableColumn()
        .withEmptyTable()
        .withBadlyNamedObjects()
        .withVarcharInsteadOfUuid()
        .withUnnecessaryWhereClause()
        .withNaturalKeys()
        .withMoneyColumn()
        .withTimestampInTheMiddle()
        .withTableWhereAllColumnsNullable();

    private final HealthLogger healthLogger = new StandardHealthLogger(
        getConnectionCredentials(), new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), new PrimaryHostDeterminerImpl()), new StandardChecksOnCluster());

    @Test
    void completenessTest() {
        assertThat(healthLogger.logAll(Exclusions.empty()))
            .as("All diagnostics must be logged")
            .hasSameSizeAs(Diagnostic.values());
    }

    @Test
    void logAllWithDefaultSchema() {
        final List<String> logs = healthLogger.logAll(Exclusions.empty());
        assertThat(logs)
            .hasSameSizeAs(Diagnostic.values());
        for (final Diagnostic diagnostic : Diagnostic.values()) {
            assertThat(logs)
                .filteredOn(ofKey(diagnostic))
                .hasSize(1)
                .containsExactly(getExpectedValueForDefaultSchema(diagnostic));
        }
    }

    @SuppressWarnings("checkstyle:LambdaBodyLength")
    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logAll(final String schemaName) {
        executeTestOnDatabase(schemaName, CONFIGURER,
            ctx -> {
                collectStatistics(schemaName);
                assertThat(healthLogger.logAll(Exclusions.empty(), ctx))
                    .hasSameSizeAs(Diagnostic.values())
                    .containsExactlyInAnyOrder(
                        "invalid_indexes:1",
                        "duplicated_indexes:2",
                        "foreign_keys_without_index:8",
                        "tables_without_primary_key:6",
                        "indexes_with_null_values:1",
                        "bloated_indexes:19",
                        "bloated_tables:4",
                        "intersected_indexes:18",
                        "unused_indexes:19",
                        "tables_with_missing_indexes:0",
                        "tables_without_description:25",
                        "columns_without_description:63",
                        "columns_with_json_type:1",
                        "columns_with_serial_types:3",
                        "functions_without_description:3",
                        "indexes_with_boolean:1",
                        "not_valid_constraints:3",
                        "btree_indexes_on_array_columns:2",
                        "sequence_overflow:3",
                        "primary_keys_with_serial_types:2",
                        "duplicated_foreign_keys:3",
                        "intersected_foreign_keys:1",
                        "possible_object_name_overflow:2",
                        "tables_not_linked_to_others:14",
                        "foreign_keys_with_unmatched_column_type:2",
                        "tables_with_zero_or_one_column:8",
                        isNotNullConstraintsSupported() ? "objects_not_following_naming_convention:36" : "objects_not_following_naming_convention:26",
                        "columns_not_following_naming_convention:7",
                        "primary_keys_with_varchar:3",
                        "columns_with_fixed_length_varchar:17",
                        "indexes_with_unnecessary_where_clause:2",
                        "primary_keys_that_most_likely_natural_keys:7",
                        "columns_with_money_type:1",
                        "indexes_with_timestamp_in_the_middle:3",
                        "columns_with_timestamp_or_timetz_type:4",
                        "tables_where_primary_key_columns_not_first:3",
                        "tables_where_all_columns_nullable_except_pk:5"
                    );
            }
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void logTablesWithMissingIndexes(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);

            assertThat(healthLogger.logAll(Exclusions.empty(), ctx))
                .hasSameSizeAs(Diagnostic.values())
                .filteredOn(ofKey(Diagnostic.TABLES_WITH_MISSING_INDEXES))
                .hasSize(1)
                .containsExactly("tables_with_missing_indexes:1");
        });
    }

    @NonNull
    private static String getExpectedValueForDefaultSchema(final CheckNameAware check) {
        final LoggingKey key = SimpleLoggingKeyAdapter.of(check);
        return key.getSubKeyName() + ":0";
    }

    @NonNull
    private static Predicate<String> ofKey(final CheckNameAware check) {
        return new SimpleLoggingKeyPredicate(SimpleLoggingKeyAdapter.of(check));
    }

    private record SimpleLoggingKeyPredicate(LoggingKey key) implements Predicate<String> {

        private SimpleLoggingKeyPredicate(final LoggingKey key) {
            this.key = Objects.requireNonNull(key);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(final String str) {
            return str.contains(key.getSubKeyName());
        }
    }
}
