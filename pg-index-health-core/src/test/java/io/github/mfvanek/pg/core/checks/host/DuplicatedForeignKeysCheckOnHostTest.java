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
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class DuplicatedForeignKeysCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<DuplicatedForeignKeys> check = new DuplicatedForeignKeysCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(DuplicatedForeignKeys.class)
            .hasDiagnostic(Diagnostic.DUPLICATED_FOREIGN_KEYS)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withDuplicatedForeignKeys(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.ofNotNullColumn(ctx, "accounts", "c_accounts_fk_client_id", "client_id"),
                        ForeignKey.ofNotNullColumn(ctx, "accounts", "c_accounts_fk_client_id_duplicate", "client_id"))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialAndForeignKeysInPartitionedTable().withDuplicatedForeignKeysInPartitionedTable(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fk_duplicate", "ref_type"),
                        ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fkey", "ref_type"))
                ));
    }
}
