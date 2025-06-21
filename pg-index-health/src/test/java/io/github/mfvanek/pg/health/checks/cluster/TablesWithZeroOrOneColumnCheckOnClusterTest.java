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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class TablesWithZeroOrOneColumnCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<TableWithColumns> check = new TablesWithZeroOrOneColumnCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(TableWithColumns.class)
            .hasDiagnostic(Diagnostic.TABLES_WITH_ZERO_OR_ONE_COLUMN)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withCheckConstraintOnSerialPrimaryKey().withEmptyTable(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    TableWithColumns.ofNotNullColumn(ctx, "another_table", "id"),
                    TableWithColumns.withoutColumns(ctx, "empty")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("another_table", "empty")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withDroppedColumnInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    TableWithColumns.ofNotNullColumn(ctx, "tp", "ref_type")
                ));
    }
}
