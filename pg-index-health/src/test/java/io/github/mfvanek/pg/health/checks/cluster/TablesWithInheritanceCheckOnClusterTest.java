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
import io.github.mfvanek.pg.model.table.Table;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class TablesWithInheritanceCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull Table> check = new TablesWithInheritanceCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Table.class)
            .hasDiagnostic(Diagnostic.TABLES_WITH_INHERITANCE)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withInheritance, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                .containsExactly(
                    Table.of(ctx, "child_table"),
                    Table.of(ctx, "\"second-child_table\""))
                .allMatch(t -> t.getTableSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("child_table", "\"second-child_table\"")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnorePartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBloatInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }
}
