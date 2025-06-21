/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipSmallTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.logging.Level;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class TablesWithMissingIndexesCheckOnClusterTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnCluster<TableWithMissingIndex> check = new TablesWithMissingIndexesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(TableWithMissingIndex.class)
            .hasDiagnostic(Diagnostic.TABLES_WITH_MISSING_INDEXES)
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    TableWithMissingIndex.of(ctx, "accounts"))
                .allMatch(t -> t.getSeqScans() >= AMOUNT_OF_TRIES)
                .allMatch(t -> t.getIndexScans() == 0)
                .allMatch(t -> t.getTableSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipSmallTablesPredicate.of(1_000_000L))
                .isEmpty();
        });
    }

    @Test
    void getResultAsUnion() {
        try (LogsCaptor ignored = new LogsCaptor(TablesWithMissingIndexesCheckOnCluster.class, Level.FINEST)) {
            final TableWithMissingIndex t1 = TableWithMissingIndex.of("t1", 1L, 10L, 1L);
            final TableWithMissingIndex t2 = TableWithMissingIndex.of("t2", 2L, 30L, 3L);
            final TableWithMissingIndex t3 = TableWithMissingIndex.of("t3", 3L, 40L, 4L);
            final List<List<TableWithMissingIndex>> tablesWithMissingIndexesFromAllHosts = List.of(
                List.of(),
                List.of(t1, t3),
                List.of(t2),
                List.of(t2, t3)
            );
            final List<TableWithMissingIndex> tablesWithMissingIndexes = TablesWithMissingIndexesCheckOnCluster.getResultAsUnion(
                tablesWithMissingIndexesFromAllHosts);
            Assertions.assertThat(tablesWithMissingIndexes)
                .hasSize(3)
                .containsExactlyInAnyOrder(t1, t2, t3);
        }
    }
}
