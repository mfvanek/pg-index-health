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

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

class InvalidIndexesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Index> check = new InvalidIndexesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Index.class)
            .hasDiagnostic(Diagnostic.INVALID_INDEXES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withInvalidIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    Index.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name_first_name")));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "clients"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "i_clients_last_name_first_name"))
                .isEmpty();
        });
    }
}
