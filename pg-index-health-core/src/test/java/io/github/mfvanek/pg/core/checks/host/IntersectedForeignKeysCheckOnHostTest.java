/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class IntersectedForeignKeysCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<DuplicatedForeignKeys> check = new IntersectedForeignKeysCheckOnHost(getPgConnection());

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
    void shouldIgnoreCompletelyIdenticalForeignKeys(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn()
            .withDuplicatedForeignKeys().withSerialAndForeignKeysInPartitionedTable().withDuplicatedForeignKeysInPartitionedTable(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withIntersectedForeignKeys(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.of(ctx, "client_preferences", "c_client_preferences_email_phone_fk",
                            List.of(Column.ofNotNull(ctx, "client_preferences", "email"), Column.ofNotNull(ctx, "client_preferences", "phone"))),
                        ForeignKey.of(ctx, "client_preferences", "c_client_preferences_phone_email_fk",
                            List.of(Column.ofNotNull(ctx, "client_preferences", "phone"), Column.ofNotNull(ctx, "client_preferences", "email"))))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "client_preferences"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithPartitionedTables(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withSerialAndForeignKeysInPartitionedTable().withIntersectedForeignKeysInPartitionedTable(), ctx ->
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.ofNotNullColumn(ctx, "t1", "t1_ref_type_fkey", "ref_type"),
                        ForeignKey.of(ctx, "t1", "t1_ref_type_ref_value_fk",
                            List.of(Column.ofNotNull(ctx, "t1", "ref_type"), Column.ofNotNull(ctx, "t1", "ref_value"))))
                ));
    }
}
