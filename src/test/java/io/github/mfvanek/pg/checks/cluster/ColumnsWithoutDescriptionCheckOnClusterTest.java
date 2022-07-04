/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnsWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnCluster<Column> check;

    ColumnsWithoutDescriptionCheckOnClusterTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new ColumnsWithoutDescriptionCheckOnCluster(
                HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase())));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Column.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION);
    }

    @Test
    void onEmptyDatabase() {
        assertThat(check.check())
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCommentOnColumns(), ctx ->
                assertThat(check.check(ctx))
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(9)
                    .containsExactly(
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_balance"),
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "account_number"),
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "deleted"),
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                            Column.ofNotNull(ctx.enrichWithSchema("clients"), "first_name"),
                            Column.ofNotNull(ctx.enrichWithSchema("clients"), "id"),
                            Column.ofNotNull(ctx.enrichWithSchema("clients"), "last_name"),
                            Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name"))
                    .filteredOn(Column::isNullable)
                    .hasSize(1)
                    .containsExactly(
                            Column.ofNullable(ctx.enrichWithSchema("clients"), "middle_name"));

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))))
                    .hasSize(4)
                    .allMatch(c -> c.getTableName().equals(ctx.enrichWithSchema("clients")));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnColumns(), ctx ->
                assertThat(check.check(ctx))
                        .hasSize(9)
                        .filteredOn(c -> c.getColumnName().equalsIgnoreCase("id"))
                        .hasSize(2)
                        .containsExactly(
                                Column.ofNotNull(ctx.enrichWithSchema("accounts"), "id"),
                                Column.ofNotNull(ctx.enrichWithSchema("clients"), "id")));
    }
}
