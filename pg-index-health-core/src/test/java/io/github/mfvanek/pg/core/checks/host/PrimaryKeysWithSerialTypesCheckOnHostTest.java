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
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.core.support.AbstractCheckOnHostAssert.assertThat;

class PrimaryKeysWithSerialTypesCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<ColumnWithSerialType> check = new PrimaryKeysWithSerialTypesCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(ColumnWithSerialType.class)
            .hasDiagnostic(Diagnostic.PRIMARY_KEYS_WITH_SERIAL_TYPES)
            .hasHost(getHost())
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withSerialType(), ctx -> {
            assertThat(check)
                .executing(ctx)
                .hasSize(1)
                .containsExactlyInAnyOrder(
                    ColumnWithSerialType.of(
                        Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "id"), SerialType.BIG_SERIAL, ctx.enrichWithSchema("bad_accounts_id_seq")
                    ));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "bad_accounts"))
                .isEmpty();

            assertThat(check)
                .executing(ctx, SkipBySequenceNamePredicate.ofName(ctx, "bad_accounts_id_seq"))
                .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withIdentityPrimaryKey(), ctx ->
            assertThat(check)
                .executing(ctx)
                .isEmpty()
        );
    }
}
