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

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class BtreeIndexesOnArrayColumnsCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<IndexWithColumns> check = new BtreeIndexesOnArrayColumnsCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withBtreeIndexesOnArrayColumn(), ctx -> {
            final String accountsTableName = ctx.enrichWithSchema("accounts");
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    IndexWithColumns.ofSingle(ctx, accountsTableName, "accounts_roles_btree_idx", 0L,
                        Column.ofNotNull(accountsTableName, "roles")),
                    IndexWithColumns.ofSingle(ctx, accountsTableName, "accounts_account_number_roles_btree_idx", 0L,
                        Column.ofNotNull(accountsTableName, "roles"))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();
        });
    }
}
