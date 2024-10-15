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

import io.github.mfvanek.pg.checks.predicates.FilterTablesByBloatPredicate;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableBloatAware;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.support.StatisticsAwareTestBase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Predicate;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

class TablesWithBloatCheckOnClusterTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnCluster<TableWithBloat> check = new TablesWithBloatCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(TableWithBloat.class)
            .hasDiagnostic(Diagnostic.BLOATED_TABLES)
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            collectStatistics(schemaName);
            Assertions.assertThat(existsStatisticsForTable(schemaName, "accounts"))
                .isTrue();

            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactlyInAnyOrder(
                    TableWithBloat.of(ctx.enrichWithSchema("accounts"), 0L, 0L, 0),
                    TableWithBloat.of(ctx.enrichWithSchema("clients"), 0L, 0L, 0))
                .allMatch(t -> t.getTableSizeInBytes() > 0L) // real size doesn't matter
                .allMatch(t -> t.getBloatPercentage() == 0 && t.getBloatSizeInBytes() == 0L);

            assertThat(check)
                .executing(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("clients")))
                .hasSize(1)
                .containsExactly(
                    TableWithBloat.of(ctx.enrichWithSchema("accounts"), 0L, 0L, 0))
                .allMatch(t -> t.getTableSizeInBytes() > 0L) // real size doesn't matter
                .allMatch(t -> t.getBloatPercentage() == 0 && t.getBloatSizeInBytes() == 0L);

            final Predicate<TableBloatAware> predicate = FilterTablesByBloatPredicate.of(0L, 10)
                .and(FilterTablesByNamePredicate.of(ctx.enrichWithSchema("clients")));
            assertThat(check)
                .executing(ctx, predicate)
                .isEmpty();
        });
    }
}
