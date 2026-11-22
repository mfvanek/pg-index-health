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
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class ForeignKeysWithUnmatchedColumnTypeCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull ForeignKey> check = new ForeignKeysWithUnmatchedColumnTypeCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ForeignKey.class)
            .hasDiagnostic(Diagnostic.FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withIntersectedForeignKeys(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(4)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.ofNullableColumn(ctx, "bad_clients", "c_bad_clients_fk_email_phone", "phone"),
                    ForeignKey.ofNullableColumn(ctx, "bad_clients", "c_bad_clients_fk_real_client_id", "real_client_id"),
                    ForeignKey.ofNotNullColumn(ctx, "client_preferences", "c_client_preferences_email_phone_fk", "\"Phone\""),
                    ForeignKey.ofNotNullColumn(ctx, "client_preferences", "c_client_preferences_phone_email_fk", "\"Phone\"")
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.of(ctx, List.of("bad_clients", "client_preferences")))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialAndForeignKeysInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(5)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    ForeignKey.ofNullableColumn(ctx, "referencing_bad_table_partitioned", "\"referencing_bad_table_partitioned-fk\"", "rbt_value"),
                    ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fk_duplicate", "ref_type"),
                    ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fkey", "ref_type"),
                    ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_ref_value_fk", "ref_type"),
                    ForeignKey.ofNullableColumn(ctx, "t3", "t3_ref_type_fkey", "ref_type")
                ));
    }
}
