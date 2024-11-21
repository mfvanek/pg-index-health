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

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.core.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.support.DatabasePopulator;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Column> check = new ColumnsWithoutDescriptionCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final String accountsTableName = ctx.enrichWithSchema("accounts");
            final String clientsTableName = ctx.enrichWithSchema("clients");
            assertThat(check)
                .executing(ctx)
                .hasSize(10)
                .containsExactly(
                    Column.ofNotNull(accountsTableName, "account_balance"),
                    Column.ofNotNull(accountsTableName, "account_number"),
                    Column.ofNotNull(accountsTableName, "client_id"),
                    Column.ofNotNull(accountsTableName, "deleted"),
                    Column.ofNotNull(accountsTableName, "id"),
                    Column.ofNotNull(clientsTableName, "first_name"),
                    Column.ofNotNull(clientsTableName, "id"),
                    Column.ofNullable(clientsTableName, "info"),
                    Column.ofNotNull(clientsTableName, "last_name"),
                    Column.ofNullable(clientsTableName, "middle_name"))
                .filteredOn(Column::isNullable)
                .hasSize(2)
                .containsExactly(
                    Column.ofNullable(clientsTableName, "info"),
                    Column.ofNullable(clientsTableName, "middle_name"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .hasSize(5)
                .allMatch(c -> c.getTableName().equals(clientsTableName));
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
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                    Column.ofNotNull(ctx.enrichWithSchema("clients"), "id")));
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
}
