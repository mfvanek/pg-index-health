/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Column> check = new ColumnsWithoutDescriptionCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Column.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            assertThat(check.check(ctx))
                .hasSize(10)
                .containsExactly(
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_balance"),
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_number"),
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "deleted"),
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                    Column.ofNotNull(ctx.enrichWithSchema("clients"), "first_name"),
                    Column.ofNotNull(ctx.enrichWithSchema("clients"), "id"),
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "info"),
                    Column.ofNotNull(ctx.enrichWithSchema("clients"), "last_name"),
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name"))
                .filteredOn(Column::isNullable)
                .hasSize(2)
                .containsExactly(
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "info"),
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name"));

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))))
                .hasSize(5)
                .allMatch(c -> c.getTableName().equals(ctx.enrichWithSchema("clients")));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnColumns(), ctx ->
            assertThat(check.check(ctx))
                .hasSize(10)
                .filteredOn(c -> "id".equalsIgnoreCase(c.getColumnName()))
                .hasSize(2)
                .containsExactly(
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                    Column.ofNotNull(ctx.enrichWithSchema("clients"), "id")));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDroppedInfoColumn(), ctx ->
            assertThat(check.check(ctx))
                .hasSize(9)
                .filteredOn(Column::isNullable)
                .hasSize(1)
                .containsExactly(
                    Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name")));
    }
}
