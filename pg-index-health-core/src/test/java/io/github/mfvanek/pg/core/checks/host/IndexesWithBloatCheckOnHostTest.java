/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.core.checks.host.IndexesWithBloatCheckOnHost;
import io.github.mfvanek.pg.core.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.predicates.SkipBloatUnderThresholdPredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallIndexesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class IndexesWithBloatCheckOnHostTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnHost<IndexWithBloat> check = new IndexesWithBloatCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithBloat.class)
            .hasDiagnostic(Diagnostic.BLOATED_INDEXES)
            .hasHost(getHost())
            .isRuntime();
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
                .containsExactlyInAnyOrder(
                    IndexWithBloat.of(accountsTableName, ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                    IndexWithBloat.of(accountsTableName, ctx.enrichWithSchema("accounts_pkey"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0),
                    IndexWithBloat.of(clientsTableName, ctx.enrichWithSchema("i_clients_email_phone"), 0L, 0L, 0))
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
}
