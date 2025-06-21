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
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithoutDescriptionCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<Column> check = new ColumnsWithoutDescriptionCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBadlyNamedObjects(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(13)
                .containsExactly(
                    Column.ofNotNull(ctx, "accounts", "account_balance"),
                    Column.ofNotNull(ctx, "accounts", "account_number"),
                    Column.ofNotNull(ctx, "accounts", "client_id"),
                    Column.ofNotNull(ctx, "accounts", "deleted"),
                    Column.ofNotNull(ctx, "accounts", "id"),
                    Column.ofNotNull(ctx, "\"bad-table\"", "\"bad-id\""),
                    Column.ofNotNull(ctx, "\"bad-table-two\"", "\"bad-ref-id\""),
                    Column.ofNullable(ctx, "\"bad-table-two\"", "description"),
                    Column.ofNotNull(ctx, "clients", "first_name"),
                    Column.ofNotNull(ctx, "clients", "id"),
                    Column.ofNullable(ctx, "clients", "info"),
                    Column.ofNotNull(ctx, "clients", "last_name"),
                    Column.ofNullable(ctx, "clients", "middle_name"))
                .filteredOn(Column::isNullable)
                .hasSize(3)
                .containsExactly(
                    Column.ofNullable(ctx, "\"bad-table-two\"", "description"),
                    Column.ofNullable(ctx, "clients", "info"),
                    Column.ofNullable(ctx, "clients", "middle_name")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "\"bad-table\"", "\"bad-table-two\"")))
                .hasSize(5)
                .allMatch(c -> c.getTableName().equals(ctx.enrichWithSchema("clients")));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnColumns(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(10)
                .filteredOn(c -> "id".equalsIgnoreCase(c.getColumnName()))
                .hasSize(2)
                .containsExactly(
                    Column.ofNotNull(ctx, "accounts", "id"),
                    Column.ofNotNull(ctx, "clients", "id")));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDroppedInfoColumn(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(9)
                .filteredOn(Column::isNullable)
                .hasSize(1)
                .containsExactly(
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name")));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        final String expectedTableName = "custom_entity_reference_with_very_very_very_long_name";
        executeTestOnDatabase(schemaName, DatabasePopulator::withPartitionedTableWithoutComments, ctx ->
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients")))
                .hasSize(4)
                .containsExactly(
                    Column.ofNotNull(ctx, expectedTableName, "creation_date"),
                    Column.ofNotNull(ctx, expectedTableName, "entity_id"),
                    Column.ofNotNull(ctx, expectedTableName, "ref_type"),
                    Column.ofNotNull(ctx, expectedTableName, "ref_value")));
    }
}
