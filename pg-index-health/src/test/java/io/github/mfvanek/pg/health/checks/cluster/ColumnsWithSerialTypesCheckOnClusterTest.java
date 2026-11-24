/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithSerialTypesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ColumnWithSerialType> check = new ColumnsWithSerialTypesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithSerialType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_SERIAL_TYPES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialType, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx, "bad_accounts", "real_account_id", "bad_accounts_real_account_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, "bad_accounts", "real_client_id", "bad_accounts_real_client_id_seq"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "bad_accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.ofNames(ctx, "bad_accounts_real_account_id_seq", "bad_accounts_real_client_id_seq"))
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
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx, "bad_accounts", "real_client_id", "bad_accounts_real_client_id_seq")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "bad_accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.ofNames(ctx, "bad_accounts_real_client_id_seq"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldIgnorePrimaryKeysAndDetectUniqueOrForeignKeySerialColumns(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialColumns, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx, "one_more_table", "id", "one_more_table_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, "test_table", "id", "test_table_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, "test_table", "num", "test_table_num_seq")
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithSerialType.ofBigSerial(ctx, "attachment", "id", "attachment_id_seq"),
                    ColumnWithSerialType.ofBigSerial(ctx, "parent", "real_client_id", "parent_real_client_id_seq")
                ));
    }
}
