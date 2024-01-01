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

import io.github.mfvanek.pg.checks.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class IndexesWithNullValuesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<IndexWithNulls> check = new IndexesWithNullValuesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(IndexWithNulls.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.INDEXES_WITH_NULL_VALUES);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(1)
                    .containsExactly(
                            IndexWithNulls.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_middle_name"), 0L, "middle_name"))
                    .allMatch(i -> i.getNullableColumn().isNullable());

            assertThat(check.check(ctx, FilterIndexesByNamePredicate.of(ctx.enrichWithSchema("i_clients_middle_name"))))
                    .isEmpty();
        });
    }
}
