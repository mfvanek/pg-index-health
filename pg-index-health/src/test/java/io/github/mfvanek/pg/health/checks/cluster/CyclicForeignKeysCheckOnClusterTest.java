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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipByConstraintNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class CyclicForeignKeysCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ForeignKeyCycle> check = new CyclicForeignKeysCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKeyCycle.class)
            .hasDiagnostic(Diagnostic.CYCLIC_FOREIGN_KEYS)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withDuplicatedForeignKeys(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCyclicForeignKeys, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(7)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "\"Cfk-A-First\"", "\"Cfk-A-First-Fk\"", "\"Second-Id\""),
                        ForeignKey.ofNullableColumn(ctx, "\"Cfk-A-Second\"", "\"Cfk-A-Second-Fk\"", "\"First-Id\"")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNullableColumn(ctx, "cfk_customers", "cfk_customers_last_order_fk", "last_order_id"),
                        ForeignKey.of(ctx, "cfk_orders", "cfk_orders_customer_fk",
                            List.of(Column.ofNotNull(ctx, "cfk_orders", "customer_id"), Column.ofNotNull(ctx, "cfk_orders", "region_id")))),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "cfk_left", "cfk_left_first_right_fk", "first_right_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_right", "cfk_right_left_fk", "left_id")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNullableColumn(ctx, "cfk_left", "cfk_left_second_right_fk", "second_right_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_right", "cfk_right_left_fk", "left_id")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "cfk_node_a", "cfk_node_a_b_fk", "b_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_node_b", "cfk_node_b_c_fk", "c_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_node_c", "cfk_node_c_a_fk", "a_id")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "cfk_ring_a", "cfk_ring_a_c_fk", "c_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_ring_c", "cfk_ring_c_a_fk", "a_id")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "cfk_ring_a", "cfk_ring_a_c_fk", "c_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_ring_c", "cfk_ring_c_b_fk", "b_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_ring_b", "cfk_ring_b_a_fk", "a_id"))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldExcludeCycleByAnyOfItsTablesOrConstraints(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCyclicForeignKeys, ctx -> {
            // A cycle should be excluded by any of its tables, not only by the first one
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "cfk_ring_b"))
                .hasSize(6)
                .noneMatch(c -> c.getTableNames().contains(ctx.enrichWithSchema("cfk_ring_b")));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("\"Cfk-A-Second\"", "cfk_orders", "cfk_right", "cfk_node_c", "cfk_ring_c")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipByConstraintNamePredicate.of(List.of("\"Cfk-A-Second-Fk\"", "cfk_orders_customer_fk", "cfk_right_left_fk", "cfk_node_b_c_fk", "cfk_ring_a_c_fk")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCyclicAndSelfReferencedForeignKeysInPartitionedTables, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKeyCycle.of(
                        ForeignKey.ofNullableColumn(ctx, "cfk_part_p", "cfk_part_p_q_fk", "q_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_part_q", "cfk_part_q_p_fk", "p_id")),
                    ForeignKeyCycle.of(
                        ForeignKey.ofNotNullColumn(ctx, "cfk_part_r", "cfk_part_r_s_fk", "s_id"),
                        ForeignKey.ofNotNullColumn(ctx, "cfk_part_s", "cfk_part_s_r_fk", "r_id"))
                ));
    }
}
