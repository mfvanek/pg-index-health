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
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TablesWithBloatCheckOnHostTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final AbstractCheckOnHost<TableWithBloat> check;

    TablesWithBloatCheckOnHostTest() {
        super(POSTGRES.getTestDatabase());
        this.check = new TablesWithBloatCheckOnHost(PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase()));
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(TableWithBloat.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.BLOATED_TABLES);
        assertThat(check.getHost()).isEqualTo(PgHostImpl.ofPrimary());
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
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(check.check(ctx))
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(existsStatisticsForTable(ctx, "accounts"))
                    .isTrue();

            assertThat(check.check(ctx))
                    .isNotNull()
                    .hasSize(2)
                    .containsExactlyInAnyOrder(
                            TableWithBloat.of(ctx.enrichWithSchema("accounts"), 0L, 0L, 0),
                            TableWithBloat.of(ctx.enrichWithSchema("clients"), 0L, 0L, 0))
                    .allMatch(t -> t.getTableSizeInBytes() > 0L) // real size doesn't matter
                    .allMatch(t -> t.getBloatPercentage() == 0 && t.getBloatSizeInBytes() == 0L);
        });
    }
}
