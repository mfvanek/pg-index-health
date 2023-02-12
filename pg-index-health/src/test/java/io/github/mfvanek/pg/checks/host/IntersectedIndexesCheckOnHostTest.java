/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class IntersectedIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<DuplicatedIndexes> check = new IntersectedIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(DuplicatedIndexes.class)
                .hasDiagnostic(Diagnostic.INTERSECTED_INDEXES)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(2)
                        .containsExactly(
                                DuplicatedIndexes.of(
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number_not_deleted"), 0L),
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"), 0L)
                                ),
                                DuplicatedIndexes.of(
                                        IndexWithSize.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_first"), 0L),
                                        IndexWithSize.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name"), 0L)))
                        .allMatch(d -> d.getTotalSize() >= 106_496L));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldFindHashIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedHashIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(1)
                        .containsExactly(
                                DuplicatedIndexes.of(
                                        IndexWithSize.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_first"), 0L),
                                        IndexWithSize.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name"), 0L)))
                        .allMatch(d -> d.getTotalSize() >= 106_496L));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void withDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }
}
