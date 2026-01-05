/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.DatabasePopulator;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class IndexesWithTimestampInTheMiddleCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull IndexWithColumns> check = new IndexesWithTimestampInTheMiddleCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.INDEXES_WITH_TIMESTAMP_IN_THE_MIDDLE)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withTimestampInTheMiddle, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("index.indexSizeInBytes")
                .containsExactly(
                    IndexWithColumns.ofColumns(ctx, "\"t-multi\"", "idx_multi_expr_first", List.of(
                        Column.ofNotNull(ctx, "\"t-multi\"", "\"date_trunc('day'::text, ts)\""),
                        Column.ofNotNull(ctx, "\"t-multi\"", "id"),
                        Column.ofNullable(ctx, "\"t-multi\"", "name"))),
                    IndexWithColumns.ofColumns(ctx, "\"t-multi\"", "idx_multi_expr_mid", List.of(
                        Column.ofNotNull(ctx, "\"t-multi\"", "id"),
                        Column.ofNotNull(ctx, "\"t-multi\"", "\"date_trunc('day'::text, ts)\""),
                        Column.ofNullable(ctx, "\"t-multi\"", "name"))),
                    IndexWithColumns.ofColumns(ctx, "\"t-multi\"", "idx_multi_mid", List.of(
                        Column.ofNotNull(ctx, "\"t-multi\"", "id"),
                        Column.ofNullable(ctx, "\"t-multi\"", "ts"),
                        Column.ofNullable(ctx, "\"t-multi\"", "name"))))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "\"t-multi\""))
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
                    IndexWithColumns.ofColumns(ctx, "tp", "i_tp_creation_date_entity_id_ref_type", List.of(
                        Column.ofNotNull(ctx, "tp", "creation_date"),
                        Column.ofNotNull(ctx, "tp", "entity_id"),
                        Column.ofNotNull(ctx, "tp", "ref_type")
                    ))
                ));
    }
}
