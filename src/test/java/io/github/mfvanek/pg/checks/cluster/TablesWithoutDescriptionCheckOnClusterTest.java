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
import io.github.mfvanek.pg.checks.predicates.FilterTablesBySizePredicate;
import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TablesWithoutDescriptionCheckOnClusterTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnCluster<Table> check;

    TablesWithoutDescriptionCheckOnClusterTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new TablesWithoutDescriptionCheckOnCluster(
                HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase())));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Table.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.TABLES_WITHOUT_DESCRIPTION);
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(2)
                    .containsExactly(
                            Table.of(ctx.enrichWithSchema("accounts"), 0L),
                            Table.of(ctx.enrichWithSchema("clients"), 0L));

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))))
                    .hasSize(1)
                    .containsExactly(
                            Table.of(ctx.enrichWithSchema("clients"), 0L))
                    .allMatch(t -> t.getTableSizeInBytes() > 0L);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldNotTakingIntoAccountBlankComments(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withBlankCommentOnTables(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(2)
                    .containsExactly(
                            Table.of(ctx.enrichWithSchema("accounts"), 0L),
                            Table.of(ctx.enrichWithSchema("clients"), 0L));

            assertThat(check.check(ctx, FilterTablesBySizePredicate.of(1_234L)))
                    .hasSize(1)
                    .containsExactly(
                            Table.of(ctx.enrichWithSchema("clients"), 0L))
                    .allMatch(t -> t.getTableSizeInBytes() > 1_234L);
        });
    }
}
