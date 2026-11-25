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

class ColumnsWithCharTypeCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull Column> check = new ColumnsWithCharTypeCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_CHAR_TYPE)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNullableCharacter(ctx, "clients", "contact_person"),
                    Column.ofCharacter(ctx, "clients", "gender"),
                    Column.ofNullableCharacter(ctx, "clients", "home_address"),
                    Column.ofNullableCharacter(ctx, "clients", "nickname"))
                .doesNotContain(
                    Column.ofNullableCharacter(ctx, "clients", "safe_word")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "clients"))
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNullableCharacter(ctx, "tp", "contact_person"),
                    Column.ofCharacter(ctx, "tp", "gender"),
                    Column.ofNullableCharacter(ctx, "tp", "home_address"),
                    Column.ofNullableCharacter(ctx, "tp", "nickname"))
                .doesNotContain(
                    Column.ofNullableCharacter(ctx, "tp", "safe_word")
                ));
    }
}
