/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithBloat;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class TablesMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final TablesMaintenanceOnHost tablesMaintenance;

    TablesMaintenanceOnHostImplTest() {
        super(embeddedPostgres.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        this.tablesMaintenance = new TablesMaintenanceOnHostImpl(pgConnection);
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDatabase() {
        final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertThat(tables, empty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertThat(tables, empty());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    tryToFindAccountByClientId(schemaName);
                    final List<TableWithMissingIndex> tables = tablesMaintenance.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(1));
                    final TableWithMissingIndex table = tables.get(0);
                    assertEquals(ctx.enrichWithSchema("accounts"), table.getTableName());
                    assertThat(table.getSeqScans(), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
                    assertEquals(0, table.getIndexScans());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertThat(tables, empty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, empty());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withTableWithoutPrimaryKey(),
                ctx -> {
                    final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(1));
                    final Table table = tables.get(0);
                    assertEquals(ctx.enrichWithSchema("bad_clients"), table.getTableName());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyShouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withMaterializedView(),
                ctx -> {
                    final List<Table> tables = tablesMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, empty());
                });
    }

    @Test
    void getTablesWithBloatOnEmptyDatabase() {
        final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat();
        assertNotNull(tables);
        assertThat(tables, empty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithBloatOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withStatistics(),
                ctx -> {
                    waitForStatisticsCollector();
                    final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat(ctx);
                    assertNotNull(tables);
                    assertThat(tables, empty());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithBloatOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withStatistics(),
                ctx -> {
                    waitForStatisticsCollector();
                    assertTrue(existsStatisticsForTable(ctx, "accounts"));

                    final List<TableWithBloat> tables = tablesMaintenance.getTablesWithBloat(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(2));
                    final TableWithBloat table = tables.get(0);
                    assertEquals(ctx.enrichWithSchema("accounts"), table.getTableName());
                    assertEquals(114688L, table.getTableSizeInBytes());
                    assertEquals(0L, table.getBloatSizeInBytes());
                    assertEquals(0, table.getBloatPercentage());
                });
    }
}
