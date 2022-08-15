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
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsWithJsonTypeCheckOnClusterTest extends SharedDatabaseTestBase {

    private final DatabaseCheckOnCluster<Column> check = new ColumnsWithJsonTypeCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Column.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.COLUMNS_WITH_JSON_TYPE);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withJsonType(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(1)
                    .containsExactly(
                            Column.ofNullable(ctx.enrichWithSchema("clients"), "info"));

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("clients"))))
                    .isEmpty();
        });
    }
}
