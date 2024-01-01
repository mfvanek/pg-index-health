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
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class DuplicatedIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<DuplicatedIndexes> check = new DuplicatedIndexesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(DuplicatedIndexes.class)
                .hasDiagnostic(Diagnostic.DUPLICATED_INDEXES)
                .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(1)
                        .containsExactly(
                                DuplicatedIndexes.of(
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L),
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number"), 0L)))
                        .allMatch(d -> d.getTotalSize() >= 16_384L));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void withHashIndexShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedHashIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void withDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void withDifferentCollationShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCustomCollation().withDuplicatedCustomCollationIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }
}
