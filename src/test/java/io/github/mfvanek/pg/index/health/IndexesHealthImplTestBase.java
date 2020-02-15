/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.index.maintenance.MaintenanceFactoryImpl;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.IndexWithSize;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
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

abstract class IndexesHealthImplTestBase extends DatabaseAwareTestBase {

    private final IndexesHealth indexesHealth;

    IndexesHealthImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(
                PgConnectionImpl.ofMaster(dataSource));
        this.indexesHealth = new IndexesHealthImpl(haPgConnection, new MaintenanceFactoryImpl());
    }

    @Test
    void getInvalidIndexesOnEmptyDatabase() {
        final List<Index> invalidIndexes = indexesHealth.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<Index> invalidIndexes = indexesHealth.getInvalidIndexes(ctx);
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
                    final List<Index> invalidIndexes = indexesHealth.getInvalidIndexes(ctx);
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
    void getDuplicatedIndexesOnEmptyDatabase() {
        final List<DuplicatedIndexes> duplicatedIndexes = indexesHealth.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexesHealth.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withDuplicatedIndex(),
                ctx -> {
                    final List<DuplicatedIndexes> duplicatedIndexes = indexesHealth.getDuplicatedIndexes(ctx);
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

    @Test
    void getIntersectedIndexesOnEmptyDatabase() {
        final List<DuplicatedIndexes> intersectedIndexes = indexesHealth.getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<DuplicatedIndexes> intersectedIndexes = indexesHealth.getIntersectedIndexes(ctx);
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
                    final List<DuplicatedIndexes> intersectedIndexes = indexesHealth.getIntersectedIndexes(ctx);
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

    @Test
    void getUnusedIndexesOnEmptyDatabase() {
        final List<UnusedIndex> unusedIndexes = indexesHealth.getUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getUnusedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final List<UnusedIndex> unusedIndexes = indexesHealth.getUnusedIndexes(ctx);
                    assertNotNull(unusedIndexes);
                    assertEquals(0, unusedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getUnusedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData().withDuplicatedIndex(),
                ctx -> {
                    final List<UnusedIndex> unusedIndexes = indexesHealth.getUnusedIndexes(ctx);
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
    void getForeignKeysNotCoveredWithIndexOnEmptyDatabase() {
        final List<ForeignKey> foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp,
                ctx -> {
                    final List<ForeignKey> foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex(ctx);
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
                    final List<ForeignKey> foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex(ctx);
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
                    final List<ForeignKey> foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex(ctx);
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
                    final List<ForeignKey> foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDatabase() {
        final List<TableWithMissingIndex> tables = indexesHealth.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertThat(tables, hasSize(0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<TableWithMissingIndex> tables = indexesHealth.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(0));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    tryToFindAccountByClientId(schemaName);
                    final List<TableWithMissingIndex> tables = indexesHealth.getTablesWithMissingIndexes(ctx);
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
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        final List<Table> tables = indexesHealth.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<Table> tables = indexesHealth.getTablesWithoutPrimaryKey(ctx);
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
                    final List<Table> tables = indexesHealth.getTablesWithoutPrimaryKey(ctx);
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

    @Test
    void getIndexesWithNullValuesOnEmptyDatabase() {
        final List<IndexWithNulls> indexes = indexesHealth.getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final List<IndexWithNulls> indexes = indexesHealth.getIndexesWithNullValues(ctx);
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
                    final List<IndexWithNulls> indexes = indexesHealth.getIndexesWithNullValues(ctx);
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
        final List<IndexWithBloat> indexes = indexesHealth.getIndexesWithBloat();
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
                    final List<IndexWithBloat> indexes = indexesHealth.getIndexesWithBloat(ctx);
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

                    final List<IndexWithBloat> indexes = indexesHealth.getIndexesWithBloat(ctx);
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

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void shouldResetCounters(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    tryToFindAccountByClientId(schemaName);
                    assertThat(getSeqScansForAccounts(ctx), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
                    indexesHealth.resetStatistics();
                    waitForStatisticsCollector();
                    assertEquals(0L, getSeqScansForAccounts(ctx));
                });
    }
}
