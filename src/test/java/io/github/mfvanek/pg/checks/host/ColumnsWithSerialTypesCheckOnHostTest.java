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
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.model.table.ColumnWithSerialType;
import io.github.mfvanek.pg.model.table.SerialType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithSerialTypesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ColumnWithSerialType> check = new ColumnsWithSerialTypesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(ColumnWithSerialType.class)
                .hasDiagnostic(Diagnostic.COLUMNS_WITH_SERIAL_TYPES)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialType, ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(2)
                        .containsExactly(
                                ColumnWithSerialType.of(
                                        Column.ofNullable(ctx.enrichWithSchema("bad_accounts"), "real_account_id"), SerialType.BIG_SERIAL, ""),
                                ColumnWithSerialType.of(
                                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_client_id"), SerialType.BIG_SERIAL, "")
                        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        // withData - skipped here below
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withSerialType().withDroppedInfoColumn(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }
}
