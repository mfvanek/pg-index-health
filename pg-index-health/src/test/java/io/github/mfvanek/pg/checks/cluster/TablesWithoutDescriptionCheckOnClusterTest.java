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

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterTablesBySizePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnClusterAssert.assertThat;

class TablesWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Table> check = new TablesWithoutDescriptionCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Table.class)
            .hasDiagnostic(Diagnostic.TABLES_WITHOUT_DESCRIPTION)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    Table.of(ctx.enrichWithSchema("accounts"), 0L),
                    Table.of(ctx.enrichWithSchema("clients"), 0L));

            assertThat(check)
                .executing(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts")))
                .hasSize(1)
                .containsExactly(
                    Table.of(ctx.enrichWithSchema("clients"), 0L))
                .allMatch(t -> t.getTableSizeInBytes() > 0L);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnTables(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .containsExactly(
                    Table.of(ctx.enrichWithSchema("accounts"), 0L),
                    Table.of(ctx.enrichWithSchema("clients"), 0L));

            assertThat(check)
                .executing(ctx, FilterTablesBySizePredicate.of(1_234L))
                .hasSize(1)
                .containsExactly(
                    Table.of(ctx.enrichWithSchema("clients"), 0L))
                .allMatch(t -> t.getTableSizeInBytes() > 1_234L);
        });
    }
}
