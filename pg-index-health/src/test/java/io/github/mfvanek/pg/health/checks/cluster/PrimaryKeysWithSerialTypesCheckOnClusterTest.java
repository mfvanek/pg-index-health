/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class PrimaryKeysWithSerialTypesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ColumnWithSerialType> check = new PrimaryKeysWithSerialTypesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithSerialType.class)
            .hasDiagnostic(Diagnostic.PRIMARY_KEYS_WITH_SERIAL_TYPES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withSerialType().withBadlyNamedObjects(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.of(
                        ctx, Column.ofNotNull(ctx, "bad_accounts", "id"), SerialType.BIG_SERIAL, "bad_accounts_id_seq"
                    ),
                    ColumnWithSerialType.of(
                        ctx, Column.ofNotNull(ctx, "\"bad-table\"", "\"bad-id\""), SerialType.SERIAL, "\"bad-table_bad-id_seq\""
                    )
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("bad_accounts", "\"bad-table\"")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.of(ctx, List.of("bad_accounts_id_seq", "\"bad-table_bad-id_seq\"")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withIdentityPrimaryKey(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVeryLongNamesInPartitionedTable, ctx -> {
            final String tableName = "entity_long_1234567890_1234567890_1234567890_1234567890_1234567";
            final String sequenceName = "entity_long_1234567890_1234567890_1234567890_1234_entity_id_seq";
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.of(ctx, Column.ofNotNull(ctx, tableName, "entity_id"), SerialType.BIG_SERIAL, sequenceName)
                );
        });
    }
}
