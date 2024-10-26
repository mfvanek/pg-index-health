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

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class ForeignKeysNotCoveredWithIndexCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ForeignKey> check = new ForeignKeysNotCoveredWithIndexCheckOnHost(getPgConnection());

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
            final String accountsTableName = ctx.enrichWithSchema("accounts");
            final String badClientsTableName = ctx.enrichWithSchema("bad_clients");
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactlyInAnyOrder(
                    ForeignKey.ofColumn(accountsTableName, "c_accounts_fk_client_id",
                        Column.ofNotNull(accountsTableName, "client_id")),
                    ForeignKey.ofColumn(badClientsTableName, "c_bad_clients_fk_real_client_id",
                        Column.ofNullable(badClientsTableName, "real_client_id")),
                    ForeignKey.of(badClientsTableName, "c_bad_clients_fk_email_phone",
                        List.of(
                            Column.ofNullable(badClientsTableName, "email"),
                            Column.ofNullable(badClientsTableName, "phone"))))
                .flatExtracting(ForeignKey::getColumnsInConstraint)
                .hasSize(4)
                .containsExactlyInAnyOrder(
                    Column.ofNotNull(accountsTableName, "client_id"),
                    Column.ofNullable(badClientsTableName, "real_client_id"),
                    Column.ofNullable(badClientsTableName, "email"),
                    Column.ofNullable(badClientsTableName, "phone"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withNonSuitableIndex(), ctx -> {
            final String accountsTableName = ctx.enrichWithSchema("accounts");
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    ForeignKey.ofColumn(accountsTableName, "c_accounts_fk_client_id",
                        Column.ofNotNull(accountsTableName, "client_id")));
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
