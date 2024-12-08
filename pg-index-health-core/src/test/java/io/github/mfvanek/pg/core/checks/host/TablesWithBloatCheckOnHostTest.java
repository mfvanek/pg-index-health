/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.StatisticsAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBloatUnderThresholdPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithBloatCheckOnHostTest extends StatisticsAwareTestBase {

    private final DatabaseCheckOnHost<TableWithBloat> check = new TablesWithBloatCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(TableWithBloat.class)
            .hasDiagnostic(Diagnostic.BLOATED_TABLES)
            .hasHost(getHost())
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
                    TableWithBloat.of(ctx, "accounts"),
                    TableWithBloat.of(ctx, "clients"))
                .allMatch(t -> t.getTableSizeInBytes() > 0L) // real size doesn't matter
                .allMatch(t -> t.getBloatPercentage() == 0 && t.getBloatSizeInBytes() == 0L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients")))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBloatUnderThresholdPredicate.of(0L, 0.1))
                .isEmpty();
        });
    }
}
