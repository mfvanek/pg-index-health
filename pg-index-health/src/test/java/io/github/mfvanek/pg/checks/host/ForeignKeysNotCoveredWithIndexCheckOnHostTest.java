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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class ForeignKeysNotCoveredWithIndexCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ForeignKey> check = new ForeignKeysNotCoveredWithIndexCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKey.class)
            .hasDiagnostic(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX)
            .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn(), ctx ->
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
                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withNonSuitableIndex(), ctx ->
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
                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")));
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
