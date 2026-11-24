/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipByConstraintNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ForeignKeysNotCoveredWithIndexCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull ForeignKey> check = new ForeignKeysNotCoveredWithIndexCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKey.class)
            .hasDiagnostic(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.ofBigint(ctx, "accounts", "c_accounts_fk_client_id", "client_id"),
                    ForeignKey.of(ctx, "bad_clients", "c_bad_clients_fk_email_phone",
                        List.of(Column.ofNullableVarchar(ctx, "bad_clients", "email"), Column.ofNullableVarchar(ctx, "bad_clients", "phone"))),
                    ForeignKey.ofNullableInteger(ctx, "bad_clients", "c_bad_clients_fk_real_client_id", "real_client_id")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "bad_clients")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithNotCoveringIndexes(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withNotCoveringIndexesForForeignKey, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.ofBigint(ctx, "accounts", "c_accounts_fk_client_id", "client_id")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipByConstraintNamePredicate.ofName("c_accounts_fk_client_id"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithCoveringIndexes(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCoveringIndexesForForeignKeys, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialAndForeignKeysInPartitionedTable, ctx -> {
            final String tableName = "referencing_bad_table_partitioned";
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.of(ctx, tableName, "\"referencing_bad_table_partitioned-fk\"", List.of(
                        Column.ofInteger(ctx, tableName, "rbt_id"),
                        Column.ofNullableText(ctx, tableName, "rbt_value")
                    )),
                    ForeignKey.ofBigint(ctx, "t1", "t1_ref_type_fk_duplicate", "ref_type"),
                    ForeignKey.ofBigint(ctx, "t1", "t1_ref_type_fkey", "ref_type")
                );
        });
    }
}
