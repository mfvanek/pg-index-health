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
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class TablesWithBloatCheckOnHostTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnHost<TableWithBloat> check = new TablesWithBloatCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(TableWithBloat.class)
                .hasDiagnostic(Diagnostic.BLOATED_TABLES)
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
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            TableWithBloat.of(ctx.enrichWithSchema("accounts"), 0L, 0L, 0),
                            TableWithBloat.of(ctx.enrichWithSchema("clients"), 0L, 0L, 0))
                    .allMatch(t -> t.getTableSizeInBytes() > 0L) // real size doesn't matter
                    .allMatch(t -> t.getBloatPercentage() == 0 && t.getBloatSizeInBytes() == 0L);
        });
    }
}
