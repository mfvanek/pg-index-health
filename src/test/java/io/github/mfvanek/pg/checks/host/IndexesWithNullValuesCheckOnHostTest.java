/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.PgHostImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class IndexesWithNullValuesCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<IndexWithNulls> check;

    IndexesWithNullValuesCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new IndexesWithNullValuesCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(IndexWithNulls.class)
                .hasDiagnostic(Diagnostic.INDEXES_WITH_NULL_VALUES)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(1)
                        .containsExactly(
                                IndexWithNulls.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_middle_name"), 0L, "middle_name"))
                        .allMatch(i -> i.getNullableColumn().isNullable()));
    }
}
