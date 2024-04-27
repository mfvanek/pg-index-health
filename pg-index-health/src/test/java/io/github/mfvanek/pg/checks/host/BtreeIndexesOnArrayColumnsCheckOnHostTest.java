/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class BtreeIndexesOnArrayColumnsCheckOnHostTest extends DatabaseAwareTestBase {

    private final BtreeIndexesOnArrayColumnsCheckOnHost check = new BtreeIndexesOnArrayColumnsCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Index.class)
                .hasDiagnostic(Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS)
                .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withBtreeIndexesOnArrayColumn(), ctx ->
            assertThat(check)
                    .executing(ctx)
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            Index.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_roles_btree_idx")),
                            Index.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_roles_btree_idx"))
                    )
        );
    }
}
