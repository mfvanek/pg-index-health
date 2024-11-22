/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class IndexesWithNullValuesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<IndexWithNulls> check = new IndexesWithNullValuesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithNulls.class)
            .hasDiagnostic(Diagnostic.INDEXES_WITH_NULL_VALUES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    IndexWithNulls.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_middle_name"), 0L, "middle_name"))
                .allMatch(i -> i.getNullableColumn().isNullable());

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "clients"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipIndexesByNamePredicate.ofName(ctx, "i_clients_middle_name"))
                .isEmpty();
        });
    }
}
