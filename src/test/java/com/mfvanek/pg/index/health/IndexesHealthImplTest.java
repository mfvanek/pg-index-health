/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactoryImpl;
import com.mfvanek.pg.model.IndexWithSize;
import com.mfvanek.pg.model.UnusedIndex;
import com.mfvanek.pg.utils.DatabasePopulator;
import com.mfvanek.pg.utils.TestExecutor;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IndexesHealthImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    private final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(
            PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase()));
    private final IndexesHealth indexesHealth = new IndexesHealthImpl(haPgConnection,
            new IndexMaintenanceFactoryImpl());

    @Test
    void getInvalidIndexesOnEmptyDatabase() {
        final var invalidIndexes = indexesHealth.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @Test
    void getInvalidIndexesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var invalidIndexes = indexesHealth.getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(0, invalidIndexes.size());
                });
    }

    @Test
    void getInvalidIndexesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createInvalidIndex();
                },
                () -> {
                    final var invalidIndexes = indexesHealth.getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(1, invalidIndexes.size());
                    final var index = invalidIndexes.get(0);
                    assertEquals("clients", index.getTableName());
                    assertEquals("i_clients_last_name_first_name", index.getIndexName());
                });
    }

    @Test
    void getDuplicatedIndexesOnEmptyDatabase() {
        final var duplicatedIndexes = indexesHealth.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var duplicatedIndexes = indexesHealth.getDuplicatedIndexes();
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var duplicatedIndexes = indexesHealth.getDuplicatedIndexes();
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
    void getIntersectedIndexesOnEmptyDatabase() {
        final var intersectedIndexes = indexesHealth.getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var intersectedIndexes = indexesHealth.getIntersectedIndexes();
                    assertNotNull(intersectedIndexes);
                    assertEquals(0, intersectedIndexes.size());
                });
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var intersectedIndexes = indexesHealth.getIntersectedIndexes();
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
    void getUnusedIndexesOnEmptyDatabase() {
        final var unusedIndexes = indexesHealth.getUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @Test
    void getUnusedIndexesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var unusedIndexes = indexesHealth.getUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertEquals(0, unusedIndexes.size());
                });
    }

    @Test
    void getUnusedIndexesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var unusedIndexes = indexesHealth.getUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertThat(unusedIndexes.size(), equalTo(3));
                    final var names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
                    assertThat(names, containsInAnyOrder("i_clients_last_first", "i_clients_last_name", "i_accounts_account_number"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDatabase() {
        final var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTables,
                () -> {
                    final var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateOnlyTablesAndReferences();
                    databasePopulator.createNotSuitableIndexForForeignKey();
                },
                () -> {
                    var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateOnlyTablesAndReferences();
                    databasePopulator.createSuitableIndexForForeignKey();
                },
                () -> {
                    var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDatabase() {
        final var tables = indexesHealth.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithMissingIndexesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var tables = indexesHealth.getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.tryToFindAccountByClientId(101);
                },
                () -> {
                    var tables = indexesHealth.getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("accounts", table.getTableName());
                    assertThat(table.getSeqScans(), greaterThanOrEqualTo(101L));
                    assertEquals(0, table.getIndexScans());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        final var tables = indexesHealth.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var tables = indexesHealth.getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createTableWithoutPrimaryKey();
                },
                () -> {
                    var tables = indexesHealth.getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("bad_clients", table.getTableName());
                });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDatabase() {
        final var indexes = indexesHealth.getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @Test
    void getIndexesWithNullValuesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var indexes = indexesHealth.getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(0, indexes.size());
                });
    }

    @Test
    void getIndexesWithNullValuesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createIndexWithNulls();
                },
                () -> {
                    final var indexes = indexesHealth.getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(1, indexes.size());
                });
    }

    private void executeTestOnDatabase(@Nonnull final Consumer<DatabasePopulator> databasePopulatorConsumer,
                                       @Nonnull final TestExecutor testExecutor)
            throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulatorConsumer.accept(databasePopulator);
            testExecutor.execute();
        }
    }
}
