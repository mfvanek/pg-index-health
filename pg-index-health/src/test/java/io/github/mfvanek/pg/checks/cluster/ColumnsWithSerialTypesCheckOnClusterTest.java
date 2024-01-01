/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsWithSerialTypesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<ColumnWithSerialType> check = new ColumnsWithSerialTypesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(ColumnWithSerialType.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.COLUMNS_WITH_SERIAL_TYPES);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialType, ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(2)
                    .containsExactly(
                            ColumnWithSerialType.ofBigSerial(
                                    Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_account_id"), String.format("%s.bad_accounts_real_account_id_seq", schemaName)),
                            ColumnWithSerialType.ofBigSerial(
                                    Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_client_id"), String.format("%s.bad_accounts_real_client_id_seq", schemaName)));

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_accounts"))))
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialType().withDroppedSerialColumn(), ctx ->
                assertThat(check.check(ctx))
                        .hasSize(1)
                        .containsExactly(
                                ColumnWithSerialType.ofBigSerial(
                                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "real_client_id"), String.format("%s.bad_accounts_real_client_id_seq", schemaName))
                        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreCheckConstraintsOnSerialPrimaryKey(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCheckConstraintOnSerialPrimaryKey, ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldDetectSerialColumnsWithUniqueConstraints(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withUniqueConstraintOnSerialColumn, ctx ->
                assertThat(check.check(ctx))
                        .hasSize(1)
                        .containsExactly(
                                ColumnWithSerialType.ofBigSerial(
                                        Column.ofNotNull(ctx.enrichWithSchema("one_more_table"), "id"), String.format("%s.one_more_table_id_seq", schemaName))
                        ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldDetectPrimaryKeysThatAreForeignKeysAsWell(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialPrimaryKeyReferencesToAnotherTable, ctx ->
                assertThat(check.check(ctx))
                        .hasSize(3)
                        .containsExactly(
                                ColumnWithSerialType.ofBigSerial(
                                        Column.ofNotNull(ctx.enrichWithSchema("one_more_table"), "id"), String.format("%s.one_more_table_id_seq", schemaName)),
                                ColumnWithSerialType.ofBigSerial(
                                        Column.ofNotNull(ctx.enrichWithSchema("test_table"), "id"), String.format("%s.test_table_id_seq", schemaName)),
                                ColumnWithSerialType.ofBigSerial(
                                        Column.ofNotNull(ctx.enrichWithSchema("test_table"), "num"), String.format("%s.test_table_num_seq", schemaName))
                        ));
    }
}
