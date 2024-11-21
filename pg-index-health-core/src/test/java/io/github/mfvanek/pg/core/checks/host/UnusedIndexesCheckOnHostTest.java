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
import io.github.mfvanek.pg.core.checks.host.UnusedIndexesCheckOnHost;
import io.github.mfvanek.pg.core.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.predicates.SkipDbObjectsByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class UnusedIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<UnusedIndex> check = new UnusedIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(UnusedIndex.class)
            .hasDiagnostic(Diagnostic.UNUSED_INDEXES)
            .hasHost(getHost())
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .containsExactlyInAnyOrder(
                    UnusedIndex.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_first"), 0L, 0),
                    UnusedIndex.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name"), 0L, 0),
                    UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number"), 0L, 0),
                    UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"), 0L, 0),
                    UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number_not_deleted"), 0L, 0),
                    UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_id_account_number_not_deleted"), 0L, 0))
                .allMatch(i -> i.getIndexSizeInBytes() > 0L)
                .allMatch(i -> i.getIndexScans() == 0);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts")
                    .and(SkipIndexesByNamePredicate.of(ctx, List.of("i_clients_last_first", "i_clients_last_name"))))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts")
                    .and(SkipDbObjectsByNamePredicate.of(List.of(ctx.enrichWithSchema("i_clients_last_first"), ctx.enrichWithSchema("i_clients_last_name")))))
                .isEmpty();
        });
    }
}
