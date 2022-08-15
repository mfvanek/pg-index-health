/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.predicates.FilterDuplicatedIndexesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatedIndexesCheckOnClusterTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnCluster<DuplicatedIndexes> check = new DuplicatedIndexesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(DuplicatedIndexes.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.DUPLICATED_INDEXES);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedIndex(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(1)
                    .containsExactly(
                            DuplicatedIndexes.of(
                                    IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("accounts_account_number_key"), 0L),
                                    IndexWithSize.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number"), 0L)))
                    .allMatch(d -> d.getTotalSize() >= 16_384L);

            assertThat(check.check(ctx, FilterDuplicatedIndexesByNamePredicate.of(ctx.enrichWithSchema("accounts_account_number_key"))))
                    .isEmpty();

            assertThat(check.check(ctx, FilterDuplicatedIndexesByNamePredicate.of(ctx.enrichWithSchema("i_accounts_account_number"))))
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withHashIndexShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedHashIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void withDifferentCollationShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCustomCollation().withDuplicatedCustomCollationIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }
}
