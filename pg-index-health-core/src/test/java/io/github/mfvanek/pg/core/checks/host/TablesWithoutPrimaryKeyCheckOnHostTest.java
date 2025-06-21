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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithoutPrimaryKeyCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<Table> check = new TablesWithoutPrimaryKeyCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Table.class)
            .hasDiagnostic(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withTableWithoutPrimaryKey().withIdentityPrimaryKey(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(Table.of(ctx, "bad_clients"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "bad_clients"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withMaterializedView(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTablesAndPartitions(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withPartitionedTableWithoutPrimaryKey(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    Table.of(ctx, "custom_entity_reference_with_very_very_very_long_name"),
                    Table.of(ctx, "custom_entity_reference_with_very_very_very_long_name_1_default")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTablesWhenOnlyPartitionsHavePrimaryKeys(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withPartitionedTableWithoutPrimaryKey().withPrimaryKeyForDefaultPartition(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    Table.of(ctx, "custom_entity_reference_with_very_very_very_long_name")));
    }
}
