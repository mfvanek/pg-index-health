/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.model.IndexWithSize;
import com.mfvanek.pg.model.PgContext;
import com.mfvanek.pg.model.UnusedIndex;
import com.mfvanek.pg.utils.DatabaseAwareTestBase;
import com.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class IndexMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private final IndexMaintenance indexMaintenance;

    IndexMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(dataSource);
        this.indexMaintenance = new IndexMaintenanceImpl(pgConnection, PgContext.ofPublic());
    }

    @Test
    void getInvalidIndexesOnEmptyDataBase() {
        final var invalidIndexes = indexMaintenance.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @Test
    void getInvalidIndexesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator -> databasePopulator.withReferences().populate(),
                () -> {
                    final var invalidIndexes = indexMaintenance.getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(0, invalidIndexes.size());
                });
    }

    @Test
    void getInvalidIndexesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().withInvalidIndex().populate(),
                () -> {
                    final var invalidIndexes = indexMaintenance.getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(1, invalidIndexes.size());
                    final var index = invalidIndexes.get(0);
                    assertEquals("clients", index.getTableName());
                    assertEquals("i_clients_last_name_first_name", index.getIndexName());
                });
    }

    @Test
    void getDuplicatedIndexesOnEmptyDataBase() {
        final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator -> databasePopulator.withReferences().populate(),
                () -> {
                    final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withDuplicatedIndex().populate(),
                () -> {
                    final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
                    assertNotNull(duplicatedIndexes);
                    assertEquals(1, duplicatedIndexes.size());
                    final var entry = duplicatedIndexes.get(0);
                    assertEquals("accounts", entry.getTableName());
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    assertThat(indexes.stream()
                                    .map(IndexWithSize::getIndexName)
                                    .collect(Collectors.toList()),
                            containsInAnyOrder("accounts_account_number_key", "i_accounts_account_number"));
                });
    }

    @Test
    void getIntersectedIndexesOnEmptyDataBase() {
        final var intersectedIndexes = indexMaintenance.getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator -> databasePopulator.withReferences().populate(),
                () -> {
                    final var intersectedIndexes = indexMaintenance.getIntersectedIndexes();
                    assertNotNull(intersectedIndexes);
                    assertEquals(0, intersectedIndexes.size());
                });
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().withDuplicatedIndex().populate(),
                () -> {
                    final var intersectedIndexes = indexMaintenance.getIntersectedIndexes();
                    assertNotNull(intersectedIndexes);
                    assertEquals(1, intersectedIndexes.size());
                    final var entry = intersectedIndexes.get(0);
                    assertEquals("clients", entry.getTableName());
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indexes = entry.getDuplicatedIndexes();
                    assertEquals(2, indexes.size());
                    assertThat(indexes.stream()
                                    .map(IndexWithSize::getIndexName)
                                    .collect(Collectors.toList()),
                            containsInAnyOrder("i_clients_last_first", "i_clients_last_name"));
                });
    }

    @Test
    void getPotentiallyUnusedIndexesOnEmptyDataBase() {
        final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @Test
    void getPotentiallyUnusedIndexesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator -> databasePopulator.withReferences().populate(),
                () -> {
                    final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertEquals(0, unusedIndexes.size());
                });
    }

    @Test
    void getPotentiallyUnusedIndexesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().withDuplicatedIndex().populate(),
                () -> {
                    final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertThat(unusedIndexes.size(), equalTo(3));
                    final var names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
                    assertThat(names, containsInAnyOrder("i_clients_last_first", "i_clients_last_name", "i_accounts_account_number"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDataBase() {
        final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem() {
        executeTestOnDatabase(DatabasePopulator::populate,
                () -> {
                    final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator -> databasePopulator.withReferences().populate(),
                () -> {
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withNonSuitableIndex().populate(),
                () -> {
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withSuitableIndex().populate(),
                () -> {
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
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

    @Test
    void getTablesWithMissingIndexesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().populate(),
                () -> {
                    final var tables = indexMaintenance.getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.withReferences().withData().populate();
                    databasePopulator.tryToFindAccountByClientId(101);
                },
                () -> {
                    var tables = indexMaintenance.getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("accounts", table.getTableName());
                    assertThat(table.getSeqScans(), greaterThanOrEqualTo(101L));
                    assertEquals(0, table.getIndexScans());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDataBase() {
        final var tables = indexMaintenance.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().populate(),
                () -> {
                    final var tables = indexMaintenance.getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().withTableWithoutPrimaryKey().populate(),
                () -> {
                    var tables = indexMaintenance.getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("bad_clients", table.getTableName());
                });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDataBase() {
        final var indexes = indexMaintenance.getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @Test
    void getIndexesWithNullValuesOnDatabaseWithoutThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().populate(),
                () -> {
                    final var indexes = indexMaintenance.getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(0, indexes.size());
                });
    }

    @Test
    void getIndexesWithNullValuesOnDatabaseWithThem() {
        executeTestOnDatabase(databasePopulator ->
                        databasePopulator.withReferences().withData().withNullValuesInIndex().populate(),
                () -> {
                    final var indexes = indexMaintenance.getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(1, indexes.size());
                });
    }
}
