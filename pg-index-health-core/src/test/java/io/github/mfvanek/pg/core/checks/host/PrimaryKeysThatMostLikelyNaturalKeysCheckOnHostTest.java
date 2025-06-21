/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class PrimaryKeysThatMostLikelyNaturalKeysCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<IndexWithColumns> check = new PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.PRIMARY_KEYS_THAT_MOST_LIKELY_NATURAL_KEYS)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withNaturalKeys, ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactly(
                    IndexWithColumns.ofColumns(ctx, "t2_composite", "t2_composite_pkey", 0L,
                        List.of(Column.ofNotNull(ctx, "t2_composite", "passport_series"), Column.ofNotNull(ctx, "t2_composite", "passport_number"))),
                    IndexWithColumns.ofColumns(ctx, "t3_composite", "t3_composite_pkey", 0L,
                        List.of(Column.ofNotNull(ctx, "t3_composite", "app_id"), Column.ofNotNull(ctx, "t3_composite", "app_number"))),
                    IndexWithColumns.ofNotNull(ctx, "\"times-of-creation\"", "\"times-of-creation_pkey\"", "\"time-of-creation\"")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("t2_composite", "t3_composite", "\"times-of-creation\"")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withVarcharInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1));
    }
}
