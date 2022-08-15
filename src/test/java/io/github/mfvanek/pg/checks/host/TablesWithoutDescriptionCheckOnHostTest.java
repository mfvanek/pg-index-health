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
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.support.DatabasePopulator;
import io.github.mfvanek.pg.support.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithoutDescriptionCheckOnHostTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnHost<Table> check = new TablesWithoutDescriptionCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Table.class)
                .hasDiagnostic(Diagnostic.TABLES_WITHOUT_DESCRIPTION)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(2)
                        .containsExactly(
                                Table.of(ctx.enrichWithSchema("accounts"), 0L),
                                Table.of(ctx.enrichWithSchema("clients"), 0L)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnTables(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(2)
                        .containsExactly(
                                Table.of(ctx.enrichWithSchema("accounts"), 0L),
                                Table.of(ctx.enrichWithSchema("clients"), 0L)));
    }
}
