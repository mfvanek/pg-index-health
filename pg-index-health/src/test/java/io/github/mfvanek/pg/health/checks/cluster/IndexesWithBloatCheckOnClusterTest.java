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
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.predicates.SkipBloatUnderThresholdPredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallIndexesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.assertj.core.api.Assertions;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class IndexesWithBloatCheckOnClusterTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull IndexWithBloat> check = new IndexesWithBloatCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithBloat.class)
            .hasDiagnostic(Diagnostic.BLOATED_INDEXES)
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            collectStatistics(schemaName);
            assertThat(check)
                .executing(ctx)
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            collectStatistics(schemaName);
            Assertions.assertThat(existsStatisticsForTable(schemaName, "accounts"))
                .isTrue();

            final String accountsTableName = ctx.enrichWithSchema("accounts");
            final String clientsTableName = ctx.enrichWithSchema("clients");
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes", "bloatSizeInBytes", "bloatPercentage")
                .containsExactly(
                    IndexWithBloat.of(ctx, accountsTableName, "accounts_account_number_key"),
                    IndexWithBloat.of(ctx, accountsTableName, "accounts_pkey"),
                    IndexWithBloat.of(ctx, clientsTableName, "clients_pkey"),
                    IndexWithBloat.of(ctx, clientsTableName, "i_clients_email_phone"))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.of(ctx, List.of("accounts_account_number_key", "accounts_pkey", "clients_pkey", "i_clients_email_phone")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBloatUnderThresholdPredicate.of(100_000L, 50.0))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipSmallIndexesPredicate.of(1_000_000L))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withBloatInPartitionedTable, ctx -> {
            collectStatistics(schemaName, List.of("orders_partitioned", "order_item_partitioned"));
            Assertions.assertThat(existsStatisticsForTable(schemaName, "orders_partitioned"))
                .isTrue();

            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes", "bloatSizeInBytes", "bloatPercentage")
                .containsExactly(
                    IndexWithBloat.of(ctx, "order_item_default", "order_item_default_order_id_idx"),
                    IndexWithBloat.of(ctx, "order_item_default", "order_item_default_pkey"),
                    IndexWithBloat.of(ctx, "order_item_default", "order_item_default_warehouse_id_idx"),
                    IndexWithBloat.of(ctx, "orders_default", "orders_default_pkey")
                )
                .allMatch(i -> i.getIndexSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "order_item_default_warehouse_id_idx"))
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes", "bloatSizeInBytes", "bloatPercentage")
                .containsExactly(
                    IndexWithBloat.of(ctx, "order_item_default", "order_item_default_order_id_idx"),
                    IndexWithBloat.of(ctx, "order_item_default", "order_item_default_pkey"),
                    IndexWithBloat.of(ctx, "orders_default", "orders_default_pkey")
                )
                .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 20.0);
        });
    }
}
