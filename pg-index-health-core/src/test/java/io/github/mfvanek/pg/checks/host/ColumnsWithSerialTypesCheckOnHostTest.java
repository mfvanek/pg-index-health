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
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithSerialTypesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ColumnWithSerialType> check = new ColumnsWithSerialTypesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithSerialType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_SERIAL_TYPES)
            .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialType, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_account_id"), String.format(Locale.ROOT, "%s.bad_accounts_real_account_id_seq", schemaName)),
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_client_id"), String.format(Locale.ROOT, "%s.bad_accounts_real_client_id_seq", schemaName))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialType().withDroppedSerialColumn(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_client_id"), String.format(Locale.ROOT, "%s.bad_accounts_real_client_id_seq", schemaName))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreCheckConstraintsOnSerialPrimaryKey(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCheckConstraintOnSerialPrimaryKey, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldDetectSerialColumnsWithUniqueConstraints(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withUniqueConstraintOnSerialColumn, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("one_more_table"), "id"), String.format(Locale.ROOT, "%s.one_more_table_id_seq", schemaName))
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldDetectPrimaryKeysThatAreForeignKeysAsWell(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialPrimaryKeyReferencesToAnotherTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("one_more_table"), "id"), String.format(Locale.ROOT, "%s.one_more_table_id_seq", schemaName)),
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("test_table"), "id"), String.format(Locale.ROOT, "%s.test_table_id_seq", schemaName)),
                    ColumnWithSerialType.ofBigSerial(
                        Column.ofNotNull(ctx.enrichWithSchema("test_table"), "num"), String.format(Locale.ROOT, "%s.test_table_num_seq", schemaName))
                ));
    }
}
