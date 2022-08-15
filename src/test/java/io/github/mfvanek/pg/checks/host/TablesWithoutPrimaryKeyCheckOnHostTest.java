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
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class TablesWithoutPrimaryKeyCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<Table> check;

    TablesWithoutPrimaryKeyCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new TablesWithoutPrimaryKeyCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Table.class)
                .hasDiagnostic(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withTableWithoutPrimaryKey(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(1)
                        .containsExactly(
                                Table.of(ctx.enrichWithSchema("bad_clients"), 0L))
                        .allMatch(t -> t.getTableSizeInBytes() == 0L));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withMaterializedView(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }
}
