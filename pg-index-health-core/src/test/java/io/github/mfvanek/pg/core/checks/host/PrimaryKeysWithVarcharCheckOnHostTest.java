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

class PrimaryKeysWithVarcharCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<IndexWithColumns> check = new PrimaryKeysWithVarcharCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(IndexWithColumns.class)
            .hasDiagnostic(Diagnostic.PRIMARY_KEYS_WITH_VARCHAR)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withSerialType().withBadlyNamedObjects().withVarcharInsteadOfUuid(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(3)
                .containsExactly(
                    IndexWithColumns.ofColumns(ctx, "t_link", "t_link_pkey", 0L, List.of(Column.ofNotNull(ctx, "t_link", "id_long"), Column.ofNotNull(ctx, "t_link", "\"id-short\""))),
                    IndexWithColumns.ofSingle(ctx, "t_varchar_long", "t_varchar_long_pkey", 0L, Column.ofNotNull(ctx, "t_varchar_long", "id_long")),
                    IndexWithColumns.ofSingle(ctx, "\"t-varchar-short\"", "\"t-varchar-short_pkey\"", 0L, Column.ofNotNull(ctx, "\"t-varchar-short\"", "\"id-short\""))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("t_link", "t_varchar_long", "\"t-varchar-short\"")))
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
                .containsExactly(
                    IndexWithColumns.ofColumns(ctx, "tp", "tp_pkey", 0L, List.of(
                        Column.ofNotNull(ctx, "tp", "creation_date"),
                        Column.ofNotNull(ctx, "tp", "ref_type"),
                        Column.ofNotNull(ctx, "tp", "entity_id")))
                )
        );
    }
}
