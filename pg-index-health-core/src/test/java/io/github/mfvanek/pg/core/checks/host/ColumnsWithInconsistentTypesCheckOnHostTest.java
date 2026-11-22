/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithInconsistentTypesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull Column> check = new ColumnsWithInconsistentTypesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_INCONSISTENT_TYPES)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withInconsistentTypes, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "\"t-int-id\"", "created_at", PgColumnType.TIMESTAMP),
                    Column.ofNotNull(ctx, "t_uuid_id", "created_at", PgColumnType.TIMESTAMPTZ),
                    Column.ofNotNull(ctx, "accounts", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "clients", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "\"t-int-id\"", "id", PgColumnType.INTEGER),
                    Column.ofNotNull(ctx, "t_uuid_id", "id", PgColumnType.UUID));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("clients", "accounts")))
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "\"t-int-id\"", "created_at", PgColumnType.TIMESTAMP),
                    Column.ofNotNull(ctx, "t_uuid_id", "created_at", PgColumnType.TIMESTAMPTZ),
                    Column.ofNotNull(ctx, "\"t-int-id\"", "id", PgColumnType.INTEGER),
                    Column.ofNotNull(ctx, "t_uuid_id", "id", PgColumnType.UUID));

            // Excluding "t_uuid_id" leaves "created_at" with a single type (timestamp): it is no longer
            // inconsistent and is dropped, while "id" keeps two distinct types and survives.
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("t_uuid_id")))
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "accounts", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "clients", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "\"t-int-id\"", "id", PgColumnType.INTEGER));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withInconsistentTypesInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "pt_one", "created_at", PgColumnType.TIMESTAMPTZ),
                    Column.ofNotNull(ctx, "pt_two", "created_at", PgColumnType.TIMESTAMP),
                    Column.ofNotNull(ctx, "accounts", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "clients", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "pt_one", "id", PgColumnType.UUID),
                    Column.ofNotNull(ctx, "pt_two", "id", PgColumnType.BIGINT)));
    }
}
