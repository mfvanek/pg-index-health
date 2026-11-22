/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithCharTypeCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull Column> check = new ColumnsWithCharTypeCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Column.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_CHAR_TYPE)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNullable(ctx, "clients", "contact_person", PgColumnType.CHARACTER),
                    Column.ofNotNull(ctx, "clients", "gender", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "clients", "home_address", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "clients", "nickname", PgColumnType.CHARACTER))
                .doesNotContain(
                    Column.ofNullable(ctx, "clients", "safe_word", PgColumnType.CHARACTER)
                ));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "clients"))
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    Column.ofNullable(ctx, "tp", "contact_person", PgColumnType.CHARACTER),
                    Column.ofNotNull(ctx, "tp", "gender", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "tp", "home_address", PgColumnType.CHARACTER),
                    Column.ofNullable(ctx, "tp", "nickname", PgColumnType.CHARACTER))
                .doesNotContain(
                    Column.ofNullable(ctx, "tp", "safe_word", PgColumnType.CHARACTER)
                ));
    }
}
