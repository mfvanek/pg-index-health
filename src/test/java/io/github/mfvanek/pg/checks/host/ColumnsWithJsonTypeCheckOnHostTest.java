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
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.support.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithJsonTypeCheckOnHostTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnHost<Column> check = new ColumnsWithJsonTypeCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Column.class)
                .hasDiagnostic(Diagnostic.COLUMNS_WITH_JSON_TYPE)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withJsonType(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(1)
                        .containsExactly(
                                Column.ofNullable(ctx.enrichWithSchema("clients"), "info")));
    }
}
