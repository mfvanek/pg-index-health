/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ForeignKeysNotCoveredWithIndexCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<ForeignKey> check = new ForeignKeysNotCoveredWithIndexCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKey.class)
            .hasDiagnostic(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    ForeignKey.ofNotNullColumn(ctx, "accounts", "c_accounts_fk_client_id", "client_id"),
                    ForeignKey.ofNullableColumn(ctx, "bad_clients", "c_bad_clients_fk_real_client_id", "real_client_id"),
                    ForeignKey.of(ctx, "bad_clients", "c_bad_clients_fk_email_phone",
                        List.of(
                            Column.ofNullable(ctx, "bad_clients", "email"),
                            Column.ofNullable(ctx, "bad_clients", "phone"))))
                .flatExtracting(ForeignKey::getColumnsInConstraint)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(ctx, "accounts", "client_id"),
                    Column.ofNullable(ctx, "bad_clients", "real_client_id"),
                    Column.ofNullable(ctx, "bad_clients", "email"),
                    Column.ofNullable(ctx, "bad_clients", "phone"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "bad_clients")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withNonSuitableIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ForeignKey.ofNotNullColumn(ctx, "accounts", "c_accounts_fk_client_id", "client_id"));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withSuitableIndex(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialAndForeignKeysInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fkey", "ref_type"),
                    ForeignKey.ofNotNullColumn(ctx, "t1_default", "t1_ref_type_fkey", "ref_type")
                ));
    }
}
