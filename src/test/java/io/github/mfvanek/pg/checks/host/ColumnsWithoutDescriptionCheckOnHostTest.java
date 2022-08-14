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
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.utils.AbstractCheckOnHostAssert.assertThat;

class ColumnsWithoutDescriptionCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<Column> check;

    ColumnsWithoutDescriptionCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new ColumnsWithoutDescriptionCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Column.class)
                .hasDiagnostic(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION)
                .hasHost(PgHostImpl.ofPrimary());
    }

    @Test
    void onEmptyDatabase() {
        assertThat(check)
                .executing()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCommentOnColumns(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(10)
                        .containsExactly(
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_balance"),
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_number"),
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "deleted"),
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                                Column.ofNotNull(ctx.enrichWithSchema("clients"), "first_name"),
                                Column.ofNotNull(ctx.enrichWithSchema("clients"), "id"),
                                Column.ofNullable(ctx.enrichWithSchema("clients"), "info"),
                                Column.ofNotNull(ctx.enrichWithSchema("clients"), "last_name"),
                                Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name"))
                        .filteredOn(Column::isNullable)
                        .hasSize(2)
                        .containsExactly(
                                Column.ofNullable(ctx.enrichWithSchema("clients"), "info"),
                                Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnColumns(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(10)
                        .filteredOn(c -> "id".equalsIgnoreCase(c.getColumnName()))
                        .hasSize(2)
                        .containsExactly(
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                                Column.ofNotNull(ctx.enrichWithSchema("clients"), "id")));
    }
}
