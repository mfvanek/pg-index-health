/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PrimaryKeysWithSerialTypesCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<ColumnWithSerialType> check = new PrimaryKeysWithSerialTypesCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(ColumnWithSerialType.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.PRIMARY_KEYS_WITH_SERIAL_TYPES);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withSerialType(), ctx -> assertThat(check.check(ctx))
            .hasSize(1)
            .containsExactlyInAnyOrder(
                ColumnWithSerialType.of(
                    Column.ofNotNull(ctx.enrichWithSchema("bad_accounts"), "id"), SerialType.BIG_SERIAL, String.format("%s.bad_accounts_id_seq", schemaName)
                )));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withIdentityPrimaryKey(), ctx -> assertThat(check.check(ctx))
            .isEmpty()
        );
    }
}
