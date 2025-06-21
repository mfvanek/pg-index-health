/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithSerialTypesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ColumnWithSerialType> check = new ColumnsWithSerialTypesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithSerialType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_SERIAL_TYPES)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialType, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx,
                        Column.ofNotNull(ctx, "bad_accounts", "real_account_id"), "bad_accounts_real_account_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx,
                        Column.ofNotNull(ctx, "bad_accounts", "real_client_id"), "bad_accounts_real_client_id_seq")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "bad_accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.of(ctx, List.of("bad_accounts_real_account_id_seq", "bad_accounts_real_client_id_seq")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnoreDroppedColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialType().withDroppedSerialColumn(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx,
                        Column.ofNotNull(ctx, "bad_accounts", "real_client_id"), "bad_accounts_real_client_id_seq")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "bad_accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.ofName(ctx, "bad_accounts_real_client_id_seq"))
                .isEmpty();
        });
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
                    ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "one_more_table", "id"), "one_more_table_id_seq")
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
                    ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "one_more_table", "id"), "one_more_table_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "test_table", "id"), "test_table_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "test_table", "num"), "test_table_num_seq")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withJsonAndSerialColumnsInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "parent", "real_client_id"), "parent_real_client_id_seq")));
    }
}
