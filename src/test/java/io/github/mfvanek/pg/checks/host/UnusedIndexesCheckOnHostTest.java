/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class UnusedIndexesCheckOnHostTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnHost<UnusedIndex> check = new UnusedIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(UnusedIndex.class)
                .hasDiagnostic(Diagnostic.UNUSED_INDEXES)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx ->
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
                        .allMatch(i -> i.getIndexScans() == 0));
    }
}
