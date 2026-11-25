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
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class IntersectedForeignKeysCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<@NonNull DuplicatedForeignKeys> check = new IntersectedForeignKeysCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(DuplicatedForeignKeys.class)
            .hasDiagnostic(Diagnostic.INTERSECTED_FOREIGN_KEYS)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withIntersectedForeignKeys().withDuplicatedForeignKeys(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.of(ctx, "client_preferences", "c_client_preferences_email_phone_fk",
                            Column.ofVarchar(ctx, "client_preferences", "email"), Column.ofVarchar(ctx, "client_preferences", "\"Phone\"")),
                        ForeignKey.of(ctx, "client_preferences", "c_client_preferences_phone_email_fk",
                            Column.ofVarchar(ctx, "client_preferences", "\"Phone\""), Column.ofVarchar(ctx, "client_preferences", "email")))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofNames(ctx, "client_preferences"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withSerialAndForeignKeysInPartitionedTable, ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.ofBigint(ctx, "t1", "t1_ref_type_fk_duplicate", "ref_type"),
                        ForeignKey.of(ctx, "t1", "t1_ref_type_ref_value_fk",
                            Column.ofBigint(ctx, "t1", "ref_type"), Column.ofVarchar(ctx, "t1", "ref_value"))),
                    DuplicatedForeignKeys.of(
                        ForeignKey.ofBigint(ctx, "t1", "t1_ref_type_fkey", "ref_type"),
                        ForeignKey.of(ctx, "t1", "t1_ref_type_ref_value_fk",
                            Column.ofBigint(ctx, "t1", "ref_type"), Column.ofVarchar(ctx, "t1", "ref_value")))
                ));
    }
}
