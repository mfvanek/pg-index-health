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
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

class ForeignKeysNotCoveredWithIndexCheckOnClusterTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final DatabaseCheckOnCluster<ForeignKey> check;

    ForeignKeysNotCoveredWithIndexCheckOnClusterTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new ForeignKeysNotCoveredWithIndexCheckOnCluster(
                HighAvailabilityPgConnectionImpl.of(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase())));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(ForeignKey.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX);
    }

    @Test
    void onEmptyDatabase() {
        assertThat(check.check())
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn(), ctx -> {
            final List<ForeignKey> foreignKeys = check.check(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            ForeignKey.ofColumn(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id",
                                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id")),
                            ForeignKey.ofColumn(ctx.enrichWithSchema("bad_clients"), "c_bad_clients_fk_real_client_id",
                                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")));
            // additional check on column nullability
            assertThat(foreignKeys.stream().flatMap(f -> f.getColumnsInConstraint().stream()))
                    .containsExactlyInAnyOrder(
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                            Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id"));

            final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))
                    .and(FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_clients")));
            assertThat(check.check(ctx, predicate))
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withForeignKeyOnNullableColumn().withNonSuitableIndex(), ctx -> {
            final List<ForeignKey> foreignKeys = check.check(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            ForeignKey.ofColumn(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id",
                                    Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id")),
                            ForeignKey.ofColumn(ctx.enrichWithSchema("bad_clients"), "c_bad_clients_fk_real_client_id",
                                    Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id")));
            // additional check on column nullability
            assertThat(foreignKeys.stream().flatMap(f -> f.getColumnsInConstraint().stream()))
                    .containsExactlyInAnyOrder(
                            Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"),
                            Column.ofNullable(ctx.enrichWithSchema("bad_clients"), "real_client_id"));

            final Predicate<TableNameAware> predicate = FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))
                    .and(FilterTablesByNamePredicate.of(ctx.enrichWithSchema("bad_clients")));
            assertThat(check.check(ctx, predicate))
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withSuitableIndex(), ctx ->
                assertThat(check.check(ctx))
                        .isNotNull()
                        .isEmpty());
    }
}
