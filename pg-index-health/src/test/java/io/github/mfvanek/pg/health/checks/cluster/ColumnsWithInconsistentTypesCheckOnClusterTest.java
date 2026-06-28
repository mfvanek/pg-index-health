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
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ColumnsWithInconsistentTypesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ColumnWithType> check = new ColumnsWithInconsistentTypesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithType.class)
            .hasDiagnostic(Diagnostic.COLUMNS_WITH_INCONSISTENT_TYPES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withInconsistentTypes, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithType.ofTimestamp(Column.ofNotNull(ctx, "\"t-int-id\"", "created_at")),
                    ColumnWithType.ofTimestamptz(Column.ofNotNull(ctx, "t_uuid_id", "created_at")),
                    ColumnWithType.ofBigint(Column.ofNotNull(ctx, "accounts", "id")),
                    ColumnWithType.ofBigint(Column.ofNotNull(ctx, "clients", "id")),
                    ColumnWithType.ofInteger(Column.ofNotNull(ctx, "\"t-int-id\"", "id")),
                    ColumnWithType.ofUuid(Column.ofNotNull(ctx, "t_uuid_id", "id")));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("clients", "accounts")))
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithType.ofTimestamp(Column.ofNotNull(ctx, "\"t-int-id\"", "created_at")),
                    ColumnWithType.ofTimestamptz(Column.ofNotNull(ctx, "t_uuid_id", "created_at")),
                    ColumnWithType.ofInteger(Column.ofNotNull(ctx, "\"t-int-id\"", "id")),
                    ColumnWithType.ofUuid(Column.ofNotNull(ctx, "t_uuid_id", "id")));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withInconsistentTypesInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(6)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ColumnWithType.ofTimestamptz(Column.ofNotNull(ctx, "pt_one", "created_at")),
                    ColumnWithType.ofTimestamp(Column.ofNotNull(ctx, "pt_two", "created_at")),
                    ColumnWithType.ofBigint(Column.ofNotNull(ctx, "accounts", "id")),
                    ColumnWithType.ofBigint(Column.ofNotNull(ctx, "clients", "id")),
                    ColumnWithType.ofUuid(Column.ofNotNull(ctx, "pt_one", "id")),
                    ColumnWithType.ofBigint(Column.ofNotNull(ctx, "pt_two", "id"))));
    }
}
