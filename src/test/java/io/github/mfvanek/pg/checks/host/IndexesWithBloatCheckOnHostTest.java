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
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class IndexesWithBloatCheckOnHostTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnHost<IndexWithBloat> check = new IndexesWithBloatCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(IndexWithBloat.class)
                .hasDiagnostic(Diagnostic.BLOATED_INDEXES)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            Assertions.assertThat(existsStatisticsForTable(ctx, "accounts"))
                    .isTrue();

            assertThat(check)
                    .executing(ctx)
                    .hasSize(3)
                    .containsExactlyInAnyOrder(
                            IndexWithBloat.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L, 0L, 0),
                            IndexWithBloat.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_pkey"), 0L, 0L, 0),
                            IndexWithBloat.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("clients_pkey"), 0L, 0L, 0))
                    .allMatch(i -> i.getIndexSizeInBytes() > 1L)
                    .allMatch(i -> i.getBloatSizeInBytes() > 1L && i.getBloatPercentage() >= 14);
        });
    }
}
