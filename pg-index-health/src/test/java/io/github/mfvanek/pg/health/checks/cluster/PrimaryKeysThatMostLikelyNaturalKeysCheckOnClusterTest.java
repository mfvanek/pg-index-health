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

class PrimaryKeysThatMostLikelyNaturalKeysCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull IndexWithColumns> check = new PrimaryKeysThatMostLikelyNaturalKeysCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.PRIMARY_KEYS_THAT_MOST_LIKELY_NATURAL_KEYS)
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
                    IndexWithColumns.of(ctx, "t2_composite", "t2_composite_pkey",
                        Column.ofText(ctx, "t2_composite", "passport_series"), Column.ofText(ctx, "t2_composite", "passport_number")),
                    IndexWithColumns.of(ctx, "t3_composite", "t3_composite_pkey",
                        Column.ofUuid(ctx, "t3_composite", "app_id"), Column.ofText(ctx, "t3_composite", "app_number")),
                    IndexWithColumns.ofTimestamptz(ctx, "\"times-of-creation\"", "\"times-of-creation_pkey\"", "\"time-of-creation\""))
                .allMatch(i -> i.getIndexSizeInBytes() > 1L);

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "t2_composite", "t3_composite", "\"times-of-creation\""))
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
                    IndexWithColumns.of(ctx, "tp", "tp_pkey",
                        Column.ofTimestamp(ctx, "tp", "creation_date"),
                        Column.ofVarchar(ctx, "tp", "ref_type"),
                        Column.ofVarchar(ctx, "tp", "entity_id"))
                ));
    }
}
