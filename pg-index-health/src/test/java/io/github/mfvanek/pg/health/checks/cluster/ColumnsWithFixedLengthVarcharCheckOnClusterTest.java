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
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithFixedLengthVarcharCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ColumnWithType> check = new ColumnsWithFixedLengthVarcharCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_FIXED_LENGTH_VARCHAR)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "accounts", "account_number")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "first_name")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "last_name")),
                    ColumnWithType.ofVarchar(Column.ofNullable(ctx, "clients", "middle_name")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "phone")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "email"))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withDroppedAccountNumberColumn, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(5)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "first_name")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "last_name")),
                    ColumnWithType.ofVarchar(Column.ofNullable(ctx, "clients", "middle_name")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "phone")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "clients", "email"))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(8)
                .usingRecursiveFieldByFieldElementComparator()
                .contains(
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "tp", "ref_type")),
                    ColumnWithType.ofVarchar(Column.ofNotNull(ctx, "tp", "entity_id"))
                ));
    }
}
