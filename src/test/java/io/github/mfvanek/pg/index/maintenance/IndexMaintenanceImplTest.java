/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.IndexWithSize;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithBloat;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class IndexMaintenanceImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres =
            PostgresExtensionFactory.database();

    private final IndexMaintenance indexMaintenance;

    IndexMaintenanceImplTest() {
        super(embeddedPostgres.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        this.indexMaintenance = new IndexMaintenanceImpl(pgConnection);
    }

    @Test
    void getInvalidIndexesOnEmptyDataBase() {
        final List<Index> invalidIndexes = indexMaintenance.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<Index> invalidIndexes = indexMaintenance.getInvalidIndexes(ctx);
                    assertNotNull(invalidIndexes);
                    assertEquals(0, invalidIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withInvalidIndex(),
                ctx -> {
                    final List<Index> invalidIndexes = indexMaintenance.getInvalidIndexes(ctx);
                    assertNotNull(invalidIndexes);
                    assertEquals(1, invalidIndexes.size());
                    final Index index = invalidIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("clients", index.getTableName());
                        assertEquals("i_clients_last_name_first_name", index.getIndexName());
                    } else {
                        assertEquals(schemaName + ".clients", index.getTableName());
                        assertEquals(schemaName + ".i_clients_last_name_first_name", index.getIndexName());
                    }
                });
    }

    @Test
    void getDuplicatedIndexesOnEmptyDataBase() {
        final List<DuplicatedIndexes> duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withDuplicatedIndex(),
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(1, duplicatedIndexes.size());
                    final DuplicatedIndexes entry = duplicatedIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", entry.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", entry.getTableName());
                    }
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final List<IndexWithSize> indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    final List<String> names = indexes.stream()
                            .map(IndexWithSize::getIndexName)
                            .collect(Collectors.toList());
                    if (isDefaultSchema(schemaName)) {
                        assertThat(names, containsInAnyOrder(
                                "accounts_account_number_key",
                                "i_accounts_account_number"));
                    } else {
                        assertThat(names, containsInAnyOrder(
                                schemaName + ".accounts_account_number_key",
                                schemaName + ".i_accounts_account_number"));
                    }
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedHashIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withDuplicatedHashIndex(),
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesWithDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withDifferentOpclassIndexes(),
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @Test
    void getIntersectedIndexesOnEmptyDataBase() {
        final List<DuplicatedIndexes> intersectedIndexes = indexMaintenance.getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<DuplicatedIndexes> intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
                    assertNotNull(intersectedIndexes);
                    assertEquals(0, intersectedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withDuplicatedIndex(),
                ctx -> {
                    final List<DuplicatedIndexes> intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
                    assertNotNull(intersectedIndexes);
                    assertEquals(1, intersectedIndexes.size());
                    final DuplicatedIndexes entry = intersectedIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("clients", entry.getTableName());
                    } else {
                        assertEquals(schemaName + ".clients", entry.getTableName());
                    }
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final List<IndexWithSize> indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    final List<String> names = indexes.stream()
                            .map(IndexWithSize::getIndexName)
                            .collect(Collectors.toList());
                    if (isDefaultSchema(schemaName)) {
                        assertThat(names, containsInAnyOrder(
                                "i_clients_last_first",
                                "i_clients_last_name"));
                    } else {
                        assertThat(names, containsInAnyOrder(
                                schemaName + ".i_clients_last_first",
                                schemaName + ".i_clients_last_name"));
                    }
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedHashIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withDuplicatedHashIndex(),
                ctx -> {
                    final List<DuplicatedIndexes> intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
                    assertNotNull(intersectedIndexes);
                    assertEquals(1, intersectedIndexes.size());
                    final DuplicatedIndexes entry = intersectedIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("clients", entry.getTableName());
                    } else {
                        assertEquals(schemaName + ".clients", entry.getTableName());
                    }
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final List<IndexWithSize> indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    final List<String> names = indexes.stream()
                            .map(IndexWithSize::getIndexName)
                            .collect(Collectors.toList());
                    if (isDefaultSchema(schemaName)) {
                        assertThat(names, containsInAnyOrder(
                                "i_clients_last_first",
                                "i_clients_last_name"));
                    } else {
                        assertThat(names, containsInAnyOrder(
                                schemaName + ".i_clients_last_first",
                                schemaName + ".i_clients_last_name"));
                    }
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesWithDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withDifferentOpclassIndexes(),
                ctx -> {
                    final List<DuplicatedIndexes> intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
                    assertNotNull(intersectedIndexes);
                    assertEquals(0, intersectedIndexes.size());
                });
    }

    @Test
    void getPotentiallyUnusedIndexesOnEmptyDataBase() {
        final List<UnusedIndex> unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getPotentiallyUnusedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<UnusedIndex> unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes(ctx);
                    assertNotNull(unusedIndexes);
                    assertEquals(0, unusedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getPotentiallyUnusedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withDuplicatedIndex(),
                ctx -> {
                    final List<UnusedIndex> unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes(ctx);
                    assertNotNull(unusedIndexes);
                    assertThat(unusedIndexes.size(), equalTo(3));
                    final Set<String> names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
                    if (isDefaultSchema(schemaName)) {
                        assertThat(names, containsInAnyOrder(
                                "i_clients_last_first",
                                "i_clients_last_name",
                                "i_accounts_account_number"));
                    } else {
                        assertThat(names, containsInAnyOrder(
                                schemaName + ".i_clients_last_first",
                                schemaName + ".i_clients_last_name",
                                schemaName + ".i_accounts_account_number"));
                    }
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDataBase() {
        final List<ForeignKey> foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp,
                ctx -> {
                    final List<ForeignKey> foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<ForeignKey> foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final ForeignKey foreignKey = foreignKeys.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", foreignKey.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", foreignKey.getTableName());
                    }
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withNonSuitableIndex(),
                ctx -> {
                    final List<ForeignKey> foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final ForeignKey foreignKey = foreignKeys.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", foreignKey.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", foreignKey.getTableName());
                    }
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withSuitableIndex(),
                ctx -> {
                    final List<ForeignKey> foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDataBase() {
        final List<TableWithMissingIndex> tables = indexMaintenance.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<TableWithMissingIndex> tables = indexMaintenance.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    tryToFindAccountByClientId(schemaName);
                    final List<TableWithMissingIndex> tables = indexMaintenance.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(1));
                    final TableWithMissingIndex table = tables.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", table.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", table.getTableName());
                    }
                    assertThat(table.getSeqScans(), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
                    assertEquals(0, table.getIndexScans());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDataBase() {
        final List<Table> tables = indexMaintenance.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<Table> tables = indexMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withTableWithoutPrimaryKey(),
                ctx -> {
                    final List<Table> tables = indexMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(1));
                    final Table table = tables.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("bad_clients", table.getTableName());
                    } else {
                        assertEquals(schemaName + ".bad_clients", table.getTableName());
                    }
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyShouldReturnNothingForMaterializedViews(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withMaterializedView(),
                ctx -> {
                    final List<Table> tables = indexMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(0));
                });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDataBase() {
        final List<IndexWithNulls> indexes = indexMaintenance.getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<IndexWithNulls> indexes = indexMaintenance.getIndexesWithNullValues(ctx);
                    assertNotNull(indexes);
                    assertEquals(0, indexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withNullValuesInIndex(),
                ctx -> {
                    final List<IndexWithNulls> indexes = indexMaintenance.getIndexesWithNullValues(ctx);
                    assertNotNull(indexes);
                    assertEquals(1, indexes.size());
                    final IndexWithNulls indexWithNulls = indexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("i_clients_middle_name", indexWithNulls.getIndexName());
                    } else {
                        assertEquals(schemaName + ".i_clients_middle_name", indexWithNulls.getIndexName());
                    }
                    assertEquals("middle_name", indexWithNulls.getNullableField());
                });
    }

    @Test
    void getIndexesWithBloatOnEmptyDataBase() {
        final List<IndexWithBloat> indexes = indexMaintenance.getIndexesWithBloat();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithBloatOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withStatistics(),
                ctx -> {
                    waitForStatisticsCollector();
                    final List<IndexWithBloat> indexes = indexMaintenance.getIndexesWithBloat(ctx);
                    assertNotNull(indexes);
                    assertEquals(0, indexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithBloatOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withStatistics(),
                ctx -> {
                    waitForStatisticsCollector();
                    assertTrue(existsStatisticsForTable(ctx, "accounts"));

                    final List<IndexWithBloat> indexes = indexMaintenance.getIndexesWithBloat(ctx);
                    assertNotNull(indexes);
                    assertEquals(3, indexes.size());
                    final IndexWithBloat index = indexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts_account_number_key", index.getIndexName());
                        assertEquals("accounts", index.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts_account_number_key", index.getIndexName());
                        assertEquals(schemaName + ".accounts", index.getTableName());
                    }
                    assertEquals(57344L, index.getIndexSizeInBytes());
                    assertEquals(8192L, index.getBloatSizeInBytes());
                    assertEquals(14, index.getBloatPercentage());
                });
    }

    @Test
    void getTablesWithBloatOnEmptyDataBase() {
        final List<TableWithBloat> tables = indexMaintenance.getTablesWithBloat();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithBloatOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withStatistics(),
                ctx -> {
                    waitForStatisticsCollector();
                    final List<TableWithBloat> tables = indexMaintenance.getTablesWithBloat(ctx);
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
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

                    final List<TableWithBloat> tables = indexMaintenance.getTablesWithBloat(ctx);
                    assertNotNull(tables);
                    assertEquals(2, tables.size());
                    final TableWithBloat table = tables.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", table.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", table.getTableName());
                    }
                    assertEquals(106496L, table.getTableSizeInBytes());
                    assertEquals(0L, table.getBloatSizeInBytes());
                    assertEquals(0, table.getBloatPercentage());
                });
    }

    @Test
    void securityTest() {
        executeTestOnDatabase("public",
                dbp -> dbp.withReferences().withData().withNullValuesInIndex(),
                ctx -> {
                    final long before = getRowsCount(ctx.getSchemaName(), "clients");
                    assertEquals(1001L, before);
                    List<IndexWithNulls> indexes = indexMaintenance.getIndexesWithNullValues
                            (PgContext.of("; truncate table clients;"));
                    assertNotNull(indexes);
                    assertThat(indexes, hasSize(0));
                    assertEquals(before, getRowsCount(ctx.getSchemaName(), "clients"));

                    indexes = indexMaintenance.getIndexesWithNullValues(PgContext.of("; select pg_sleep(100000000);"));
                    assertNotNull(indexes);
                    assertThat(indexes, hasSize(0));

                    indexes = indexMaintenance.getIndexesWithNullValues(ctx);
                    assertNotNull(indexes);
                    assertThat(indexes, hasSize(1));
                });
    }
}
