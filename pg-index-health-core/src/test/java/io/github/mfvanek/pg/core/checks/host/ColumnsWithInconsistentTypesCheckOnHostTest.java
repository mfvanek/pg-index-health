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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
                    Column.ofTimestamp(ctx, "\"t-int-id\"", "created_at"),
                    Column.ofTimestamptz(ctx, "t_uuid_id", "created_at"),
                    Column.ofBigint(ctx, "accounts", "id"),
                    Column.ofBigint(ctx, "clients", "id"),
                    Column.ofInteger(ctx, "\"t-int-id\"", "id"),
                    Column.ofUuid(ctx, "t_uuid_id", "id"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "clients", "accounts"))
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofTimestamp(ctx, "\"t-int-id\"", "created_at"),
                    Column.ofTimestamptz(ctx, "t_uuid_id", "created_at"),
                    Column.ofInteger(ctx, "\"t-int-id\"", "id"),
                    Column.ofUuid(ctx, "t_uuid_id", "id"));

            // Excluding "t_uuid_id" leaves "created_at" with a single type (timestamp): it is no longer
            // inconsistent and is dropped, while "id" keeps two distinct types and survives.
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "t_uuid_id"))
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofBigint(ctx, "accounts", "id"),
                    Column.ofBigint(ctx, "clients", "id"),
                    Column.ofInteger(ctx, "\"t-int-id\"", "id"));
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
                    Column.ofTimestamptz(ctx, "pt_one", "created_at"),
                    Column.ofTimestamp(ctx, "pt_two", "created_at"),
                    Column.ofBigint(ctx, "accounts", "id"),
                    Column.ofBigint(ctx, "clients", "id"),
                    Column.ofUuid(ctx, "pt_one", "id"),
                    Column.ofBigint(ctx, "pt_two", "id")));
    }
}
