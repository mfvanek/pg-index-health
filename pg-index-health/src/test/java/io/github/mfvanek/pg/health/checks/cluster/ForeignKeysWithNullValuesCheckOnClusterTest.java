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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class ForeignKeysWithNullValuesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<@NonNull ForeignKey> check = new ForeignKeysWithNullValuesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKey.class)
            .hasDiagnostic(Diagnostic.FOREIGN_KEYS_WITH_NULL_VALUES)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withCompositeForeignKey(), ctx -> {
            final String tableName = "referencing_bad_table";
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.of(ctx, "bad_clients", "c_bad_clients_fk_email_phone", List.of(
                        Column.ofNullable(ctx, "bad_clients", "email"),
                        Column.ofNullable(ctx, "bad_clients", "phone"))),
                    ForeignKey.of(ctx, tableName, "\"referencing-bad-table-fk\"", List.of(
                        Column.ofNotNull(ctx, tableName, "rbt_id"),
                        Column.ofNullable(ctx, tableName, "rbt_value")
                    ))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of(tableName, "bad_clients")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withCompositeForeignKeyInPartitionedTable, ctx -> {
            final String tableName = "referencing_bad_table_partitioned";
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.of(ctx, tableName, "\"referencing_bad_table_partitioned-fk\"", List.of(
                        Column.ofNotNull(ctx, tableName, "rbt_id"),
                        Column.ofNullable(ctx, tableName, "rbt_value")
                    ))
                );
        });
    }
}
