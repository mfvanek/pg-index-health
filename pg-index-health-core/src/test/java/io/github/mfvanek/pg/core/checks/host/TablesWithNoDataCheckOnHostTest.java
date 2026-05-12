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
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.table.Table;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithNoDataCheckOnHostTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull Table> check = new TablesWithNoDataCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Table.class)
            .hasDiagnostic(Diagnostic.TABLES_WITH_NO_DATA)
            .hasHost(getHost())
            .isRuntime();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withEmptyTable().withData(), ctx -> {
            collectStatistics(schemaName);

            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                .containsExactly(Table.of(ctx, "empty"))
                .allMatch(t -> t.getTableSizeInBytes() >= 0L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "empty"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withData().withEmptyPartitionedTable(), ctx -> {
            collectStatistics(schemaName);

            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                .containsExactly(Table.of(ctx, "partitioned_table_with_no_data"))
                .allMatch(t -> t.getTableSizeInBytes() >= 0L);
        });
    }
}
