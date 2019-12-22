/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.model.IndexWithSize;
import com.mfvanek.pg.model.UnusedIndex;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import com.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class IndexMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private final IndexMaintenance indexMaintenance;

    IndexMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(dataSource);
        this.indexMaintenance = new IndexMaintenanceImpl(pgConnection);
    }

    @Test
    void getInvalidIndexesOnEmptyDataBase() {
        final var invalidIndexes = indexMaintenance.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final var invalidIndexes = indexMaintenance.getInvalidIndexes(ctx);
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
                    final var invalidIndexes = indexMaintenance.getInvalidIndexes(ctx);
                    assertNotNull(invalidIndexes);
                    assertEquals(1, invalidIndexes.size());
                    final var index = invalidIndexes.get(0);
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
        final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
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
                    final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes(ctx);
                    assertNotNull(duplicatedIndexes);
                    assertEquals(1, duplicatedIndexes.size());
                    final var entry = duplicatedIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("accounts", entry.getTableName());
                    } else {
                        assertEquals(schemaName + ".accounts", entry.getTableName());
                    }
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    final var names = indexes.stream()
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
    void getIntersectedIndexesOnEmptyDataBase() {
        final var intersectedIndexes = indexMaintenance.getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final var intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
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
                    final var intersectedIndexes = indexMaintenance.getIntersectedIndexes(ctx);
                    assertNotNull(intersectedIndexes);
                    assertEquals(1, intersectedIndexes.size());
                    final var entry = intersectedIndexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("clients", entry.getTableName());
                    } else {
                        assertEquals(schemaName + ".clients", entry.getTableName());
                    }
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    final var names = indexes.stream()
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
    void getPotentiallyUnusedIndexesOnEmptyDataBase() {
        final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getPotentiallyUnusedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                DatabasePopulator::withReferences,
                ctx -> {
                    final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes(ctx);
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
                    final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes(ctx);
                    assertNotNull(unusedIndexes);
                    assertThat(unusedIndexes.size(), equalTo(3));
                    final var names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
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
        final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp,
                ctx -> {
                    final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
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
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
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
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
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
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDataBase() {
        final var tables = indexMaintenance.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final var tables = indexMaintenance.getTablesWithMissingIndexes(ctx);
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
                    tryToFindAccountByClientId(schemaName, AMOUNT_OF_TRIES);
                    var tables = indexMaintenance.getTablesWithMissingIndexes(ctx);
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
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
        final var tables = indexMaintenance.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final var tables = indexMaintenance.getTablesWithoutPrimaryKey(ctx);
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
                    var tables = indexMaintenance.getTablesWithoutPrimaryKey(ctx);
                    assertNotNull(tables);
                    assertThat(tables, hasSize(1));
                    var table = tables.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("bad_clients", table.getTableName());
                    } else {
                        assertEquals(schemaName + ".bad_clients", table.getTableName());
                    }
                });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDataBase() {
        final var indexes = indexMaintenance.getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName,
                dbp -> dbp.withReferences().withData(),
                ctx -> {
                    final var indexes = indexMaintenance.getIndexesWithNullValues(ctx);
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
                    final var indexes = indexMaintenance.getIndexesWithNullValues(ctx);
                    assertNotNull(indexes);
                    assertEquals(1, indexes.size());
                    final var indexWithNulls = indexes.get(0);
                    if (isDefaultSchema(schemaName)) {
                        assertEquals("i_clients_middle_name", indexWithNulls.getIndexName());
                    } else {
                        assertEquals(schemaName + ".i_clients_middle_name", indexWithNulls.getIndexName());
                    }
                    assertEquals("middle_name", indexWithNulls.getNullableField());
                });
    }
}
