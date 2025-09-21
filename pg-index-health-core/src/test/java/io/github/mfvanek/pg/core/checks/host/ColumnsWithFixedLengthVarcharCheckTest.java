/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithFixedLengthVarcharCheckTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull Column> check = new ColumnsWithFixedLengthVarcharCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_FIXED_LENGTH_VARCHAR)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(ctx, "accounts", "account_number"),
                    Column.ofNotNull(ctx, "clients", "first_name"),
                    Column.ofNotNull(ctx, "clients", "last_name"),
                    Column.ofNullable(ctx, "clients", "middle_name"),
                    Column.ofNotNull(ctx, "clients", "phone"),
                    Column.ofNotNull(ctx, "clients", "email")));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withDroppedAccountNumberColumn, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(5)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(ctx, "clients", "first_name"),
                    Column.ofNotNull(ctx, "clients", "last_name"),
                    Column.ofNullable(ctx, "clients", "middle_name"),
                    Column.ofNotNull(ctx, "clients", "phone"),
                    Column.ofNotNull(ctx, "clients", "email"))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(8)
                .contains(
                    Column.ofNotNull(ctx, "tp", "ref_type"),
                    Column.ofNotNull(ctx, "tp", "entity_id"))
        );
    }
}
