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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class IndexesWithTimestampInTheMiddleCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull IndexWithColumns> check = new IndexesWithTimestampInTheMiddleCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.INDEXES_WITH_TIMESTAMP_IN_THE_MIDDLE)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withNaturalKeysAndTimestampInTheMiddle, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes")
                .containsExactly(
                    IndexWithColumns.of(ctx, "\"t-multi\"", "idx_multi_expr_first",
                        Column.ofTimestamp(ctx, "\"t-multi\"", "\"date_trunc('day'::text, ts)\""),
                        Column.ofInteger(ctx, "\"t-multi\"", "id"),
                        Column.ofNullableText(ctx, "\"t-multi\"", "name")),
                    IndexWithColumns.of(ctx, "\"t-multi\"", "idx_multi_expr_mid",
                        Column.ofInteger(ctx, "\"t-multi\"", "id"),
                        Column.ofTimestamp(ctx, "\"t-multi\"", "\"date_trunc('day'::text, ts)\""),
                        Column.ofNullableText(ctx, "\"t-multi\"", "name")),
                    IndexWithColumns.of(ctx, "\"t-multi\"", "idx_multi_mid",
                        Column.ofInteger(ctx, "\"t-multi\"", "id"),
                        Column.ofNullableTimestamp(ctx, "\"t-multi\"", "ts"),
                        Column.ofNullableText(ctx, "\"t-multi\"", "name")))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "\"t-multi\""))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    IndexWithColumns.of(ctx, "tp", "i_tp_creation_date_entity_id_ref_type",
                        Column.ofTimestamp(ctx, "tp", "creation_date"),
                        Column.ofVarchar(ctx, "tp", "entity_id"),
                        Column.ofVarchar(ctx, "tp", "ref_type"))
                ));
    }
}
