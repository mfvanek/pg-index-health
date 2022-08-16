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

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.support.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TablesWithoutPrimaryKeyCheckOnClusterTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnCluster<Table> check = new TablesWithoutPrimaryKeyCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Table.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withTableWithoutPrimaryKey(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(1)
                    .containsExactly(Table.of(ctx.enrichWithSchema("bad_clients"), 0L))
                    .allMatch(t -> t.getTableSizeInBytes() == 0L);

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_clients"))))
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withMaterializedView(), ctx ->
                assertThat(check.check())
                        .isEmpty());
    }
}
