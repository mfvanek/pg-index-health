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

class ColumnsWithoutDescriptionCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull Column> check = new ColumnsWithoutDescriptionCheckOnHost(getPgConnection());

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
                .hasSize(21)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "\"1st_table\"", "\"2nd_id\"", PgColumnType.INTEGER),
                    Column.ofNotNull(ctx, "accounts", "account_balance", PgColumnType.NUMERIC),
                    Column.ofNotNull(ctx, "accounts", "account_number", PgColumnType.VARCHAR),
                    Column.ofNotNull(ctx, "accounts", "client_id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "accounts", "deleted", PgColumnType.BOOLEAN),
                    Column.ofNotNull(ctx, "accounts", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "\"bad-table\"", "\"bad-id\"", PgColumnType.INTEGER),
                    Column.ofNotNull(ctx, "\"bad-table-two\"", "\"bad-ref-id\"", PgColumnType.INTEGER),
                    Column.ofNullable(ctx, "\"bad-table-two\"", "description", PgColumnType.TEXT),
                    Column.ofNullable(ctx, "clients", "contact_person", PgColumnType.CHARACTER),
                    Column.ofNotNull(ctx, "clients", "first_name", PgColumnType.VARCHAR),
                    Column.ofNotNull(ctx, "clients", "gender", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "clients", "home_address", PgColumnType.CHARACTER),
                    Column.ofNotNull(ctx, "clients", "id", PgColumnType.BIGINT),
                    Column.ofNullable(ctx, "clients", "info", PgColumnType.JSONB),
                    Column.ofNotNull(ctx, "clients", "last_name", PgColumnType.VARCHAR),
                    Column.ofNullable(ctx, "clients", "middle_name", PgColumnType.VARCHAR),
                    Column.ofNullable(ctx, "clients", "nickname", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "clients", "safe_word", PgColumnType.CHARACTER),
                    Column.ofNotNull(ctx, "\"ext-table\"", "id", PgColumnType.INTEGER),
                    Column.ofNullable(ctx, "\"UpperCaseTable\"", "\"UpperCaseId\"", PgColumnType.INTEGER));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("\"1st_table\"", "accounts", "\"bad-table\"", "\"bad-table-two\"")))
                .hasSize(12);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnTablesAndColumns(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(15)
                .filteredOn(c -> "id".equalsIgnoreCase(c.getColumnName()))
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, "accounts", "id", PgColumnType.BIGINT),
                    Column.ofNotNull(ctx, "clients", "id", PgColumnType.BIGINT)));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDroppedInfoColumn(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(14)
                .usingRecursiveFieldByFieldElementComparator()
                .doesNotContain(
                    Column.ofNullable(ctx, "clients", "info", PgColumnType.JSONB))
                .noneMatch(c -> "info".equalsIgnoreCase(c.getColumnName())));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        final String expectedTableName = "custom_entity_reference_with_very_very_very_long_name";
        executeTestOnDatabase(schemaName, DatabasePopulator::withPartitionedTableWithoutComments, ctx ->
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients")))
                .hasSize(5)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNotNull(ctx, expectedTableName, "creation_date", PgColumnType.TIMESTAMPTZ),
                    Column.ofNullable(ctx, expectedTableName, "description", PgColumnType.TEXT),
                    Column.ofNotNull(ctx, expectedTableName, "entity_id", PgColumnType.VARCHAR),
                    Column.ofNotNull(ctx, expectedTableName, "ref_type", PgColumnType.VARCHAR),
                    Column.ofNotNull(ctx, expectedTableName, "ref_value", PgColumnType.VARCHAR)
                ));
    }
}
