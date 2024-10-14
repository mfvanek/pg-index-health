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

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.support.StatisticsAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithMissingIndexesCheckOnHostTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnHost<TableWithMissingIndex> check = new TablesWithMissingIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(TableWithMissingIndex.class)
            .hasDiagnostic(Diagnostic.TABLES_WITH_MISSING_INDEXES)
            .hasHost(getHost())
            .isRuntimeOnly();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    TableWithMissingIndex.of(ctx.enrichWithSchema("accounts"), 0L, 0L, 0L))
                .allMatch(t -> t.getSeqScans() >= AMOUNT_OF_TRIES)
                .allMatch(t -> t.getIndexScans() == 0)
                .allMatch(t -> t.getTableSizeInBytes() > 1L);
        });
    }
}
