/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class IndexesWithUnnecessaryWhereClauseCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull IndexWithColumns> check = new IndexesWithUnnecessaryWhereClauseCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.INDEXES_WITH_UNNECESSARY_WHERE_CLAUSE)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withUnnecessaryWhereClause, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes")
                .containsExactly(
                    IndexWithColumns.of(ctx, "t1", "idx_t1_id_ref", Column.ofBigint(ctx, "t1", "id_ref")),
                    IndexWithColumns.of(ctx, "t2", "\"idx_t2_first-ref_second_ref\"",
                        Column.ofBigint(ctx, "t2", "second_ref"), Column.ofBigint(ctx, "t2", "\"first-ref\"")))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "t1", "t2"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    IndexWithColumns.of(ctx, "deliveries", "\"idx_deliveries_status-id\"",
                        Column.ofNotNull(ctx, "deliveries", "status", ctx.enrichWithSchema("delivery_status")), Column.ofBigint(ctx, "deliveries", "id"))
                ));
    }
}
