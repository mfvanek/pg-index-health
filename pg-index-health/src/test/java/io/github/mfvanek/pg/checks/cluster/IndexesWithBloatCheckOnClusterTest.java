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

import io.github.mfvanek.pg.checks.predicates.FilterIndexesByBloatPredicate;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesBySizePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexSizeAware;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.support.DatabasePopulator;
import io.github.mfvanek.pg.support.StatisticsAwareTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Predicate;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

class IndexesWithBloatCheckOnClusterTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnCluster<IndexWithBloat> check = new IndexesWithBloatCheckOnCluster(getHaPgConnection());

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

    @SuppressWarnings("checkstyle:LambdaBodyLength")
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
                .containsExactlyInAnyOrder(
                    IndexWithBloat.of(accountsTableName, ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                    IndexWithBloat.of(accountsTableName, ctx.enrichWithSchema("accounts_pkey"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("i_clients_email_phone"), 0L, 0L, 0))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);

            final Predicate<IndexSizeAware> predicate = FilterIndexesBySizePredicate.of(1L)
                .and(FilterIndexesByNamePredicate.of(ctx.enrichWithSchema("accounts_pkey")));
            assertThat(check)
                .executing(ctx, predicate)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    IndexWithBloat.of(accountsTableName, ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("i_clients_email_phone"), 0L, 0L, 0))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);

            assertThat(check)
                .executing(ctx, FilterIndexesByBloatPredicate.of(1_000_000L, 50))
                .isEmpty();
        });
    }
}
