/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatedIndexesCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<DuplicatedIndexes> check;

    DuplicatedIndexesCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new DuplicatedIndexesCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(DuplicatedIndexes.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.DUPLICATED_INDEXES);
        assertThat(check.getHost()).isEqualTo(PgHostImpl.ofPrimary());
    }

    @Test
    void onEmptyDatabase() {
        assertThat(check.check())
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .hasSize(1)
                        .containsExactly(
                                DuplicatedIndexes.of(
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L),
                                        IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number"), 0L)))
                        .allMatch(d -> d.getTotalSize() >= 16_384L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withHashIndexShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedHashIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withDifferentCollationShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCustomCollation().withDuplicatedCustomCollationIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }
}