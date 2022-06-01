/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.table.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TablesMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    private final TablesMaintenanceOnHost tablesMaintenance;

    TablesMaintenanceOnHostImplTest() {
        super(POSTGRES.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase());
        this.tablesMaintenance = new TablesMaintenanceOnHostImpl(pgConnection);
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDatabase() {
        final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes();
        assertThat(tables)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes(ctx);
            assertThat(tables)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            tryToFindAccountByClientId(schemaName);
            final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes(ctx);
            assertThat(tables)
                    .isNotNull()
                    .hasSize(1);
            final TableWithMissingIndex table = tables.get(0);
            assertThat(table.getTableName()).isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(table.getSeqScans()).isGreaterThanOrEqualTo(AMOUNT_OF_TRIES);
            assertThat(table.getIndexScans()).isZero();
        });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey();
        assertThat(tables)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
            assertThat(tables)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withTableWithoutPrimaryKey(), ctx -> {
            final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
            assertThat(tables)
                    .isNotNull()
                    .hasSize(1);
            final Table table = tables.get(0);
            assertThat(table.getTableName()).isEqualTo(ctx.enrichWithSchema("bad_clients"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyShouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withMaterializedView(), ctx -> {
            final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
            assertThat(tables)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @Test
    void getTablesWithBloatOnEmptyDatabase() {
        final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat();
        assertThat(tables)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithBloatOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat(ctx);
            assertThat(tables)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithBloatOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(existsStatisticsForTable(ctx, "accounts")).isTrue();
            final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat(ctx);
            assertThat(tables)
                    .isNotNull()
                    .hasSize(2);
            final TableWithBloat table = tables.get(0);
            assertThat(table.getTableName()).isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(table.getTableSizeInBytes()).isEqualTo(114_688L);
            assertThat(table.getBloatSizeInBytes()).isZero();
            assertThat(table.getBloatPercentage()).isZero();
        });
    }
}
