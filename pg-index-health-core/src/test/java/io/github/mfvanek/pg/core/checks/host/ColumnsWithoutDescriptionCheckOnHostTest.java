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
                    Column.ofInteger(ctx, "\"1st_table\"", "\"2nd_id\""),
                    Column.ofNotNull(ctx, "accounts", "account_balance", PgColumnType.NUMERIC),
                    Column.ofVarchar(ctx, "accounts", "account_number"),
                    Column.ofBigint(ctx, "accounts", "client_id"),
                    Column.ofBoolean(ctx, "accounts", "deleted"),
                    Column.ofBigint(ctx, "accounts", "id"),
                    Column.ofInteger(ctx, "\"bad-table\"", "\"bad-id\""),
                    Column.ofInteger(ctx, "\"bad-table-two\"", "\"bad-ref-id\""),
                    Column.ofNullableText(ctx, "\"bad-table-two\"", "description"),
                    Column.ofNullableCharacter(ctx, "clients", "contact_person"),
                    Column.ofVarchar(ctx, "clients", "first_name"),
                    Column.ofCharacter(ctx, "clients", "gender"),
                    Column.ofNullableCharacter(ctx, "clients", "home_address"),
                    Column.ofBigint(ctx, "clients", "id"),
                    Column.ofNullable(ctx, "clients", "info", PgColumnType.JSONB),
                    Column.ofVarchar(ctx, "clients", "last_name"),
                    Column.ofNullableVarchar(ctx, "clients", "middle_name"),
                    Column.ofNullableCharacter(ctx, "clients", "nickname"),
                    Column.ofNullableCharacter(ctx, "clients", "safe_word"),
                    Column.ofInteger(ctx, "\"ext-table\"", "id"),
                    Column.ofNullableInteger(ctx, "\"UpperCaseTable\"", "\"UpperCaseId\""));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "\"1st_table\"", "accounts", "\"bad-table\"", "\"bad-table-two\""))
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
                    Column.ofBigint(ctx, "accounts", "id"),
                    Column.ofBigint(ctx, "clients", "id")));
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
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts", "clients"))
                .hasSize(5)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofTimestamptz(ctx, expectedTableName, "creation_date"),
                    Column.ofNullableText(ctx, expectedTableName, "description"),
                    Column.ofVarchar(ctx, expectedTableName, "entity_id"),
                    Column.ofVarchar(ctx, expectedTableName, "ref_type"),
                    Column.ofVarchar(ctx, expectedTableName, "ref_value")
                ));
    }
}
