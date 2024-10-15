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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Predicate;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

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
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    ForeignKey.ofColumn(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id",
                        Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id")),
                    ForeignKey.ofColumn(ctx.enrichWithSchema("bad_clients"), "c_bad_clients_fk_real_client_id",
                        Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")))
                .flatExtracting(ForeignKey::getColumnsInConstraint)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id"));

            final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))
                .and(FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_clients")));
            assertThat(check)
                .executing(ctx, predicate)
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withNonSuitableIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    ForeignKey.ofColumn(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id",
                        Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id")),
                    ForeignKey.ofColumn(ctx.enrichWithSchema("bad_clients"), "c_bad_clients_fk_real_client_id",
                        Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")))
                .flatExtracting(ForeignKey::getColumnsInConstraint)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id"));

            final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))
                .and(FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_clients")));
            assertThat(check)
                .executing(ctx, predicate)
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
}
