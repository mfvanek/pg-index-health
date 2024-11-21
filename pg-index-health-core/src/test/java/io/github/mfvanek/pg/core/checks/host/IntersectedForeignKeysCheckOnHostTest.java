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

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.core.checks.host.IntersectedForeignKeysCheckOnHost;
import io.github.mfvanek.pg.core.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

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
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withDuplicatedForeignKeys(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withIntersectedForeignKeys(), ctx -> {
            final String expectedTableName = ctx.enrichWithSchema("client_preferences");
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactly(
                    DuplicatedForeignKeys.of(
                        ForeignKey.of(expectedTableName, "c_client_preferences_email_phone_fk",
                            List.of(Column.ofNotNull(expectedTableName, "email"), Column.ofNotNull(expectedTableName, "phone"))),
                        ForeignKey.of(expectedTableName, "c_client_preferences_phone_email_fk",
                            List.of(Column.ofNotNull(expectedTableName, "phone"), Column.ofNotNull(expectedTableName, "email"))))
                );

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "client_preferences"))
                .isEmpty();
        });
    }
}
