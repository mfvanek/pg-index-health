/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

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

class IndexMaintenanceImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    private final IndexMaintenance indexMaintenance = new IndexMaintenanceImpl(
            embeddedPostgres.getTestDatabase());

    @Test
    void getInvalidIndicesOnEmptyDataBase() {
        final var invalidIndices = indexMaintenance.getInvalidIndices();
        assertNotNull(invalidIndices);
        assertEquals(0, invalidIndices.size());
    }

    @Test
    void getInvalidIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var invalidIndices = indexMaintenance.getInvalidIndices();
                    assertNotNull(invalidIndices);
                    assertEquals(0, invalidIndices.size());
                });
    }

    @Test
    void getInvalidIndicesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createInvalidIndex();
                },
                () -> {
                    final var invalidIndices = indexMaintenance.getInvalidIndices();
                    assertNotNull(invalidIndices);
                    assertEquals(1, invalidIndices.size());
                    final var index = invalidIndices.get(0);
                    assertEquals("clients", index.getTableName());
                    assertEquals("i_clients_last_name_first_name", index.getIndexName());
                });
    }

    @Test
    void getDuplicatedIndicesOnEmptyDataBase() {
        final var duplicatedIndices = indexMaintenance.getDuplicatedIndices();
        assertNotNull(duplicatedIndices);
        assertEquals(0, duplicatedIndices.size());
    }

    @Test
    void getDuplicatedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var duplicatedIndices = indexMaintenance.getDuplicatedIndices();
                    assertNotNull(duplicatedIndices);
                    assertEquals(0, duplicatedIndices.size());
                });
    }

    @Test
    void getDuplicatedIndicesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var duplicatedIndices = indexMaintenance.getDuplicatedIndices();
                    assertNotNull(duplicatedIndices);
                    assertEquals(1, duplicatedIndices.size());
                    final var entry = duplicatedIndices.get(0);
                    assertEquals("accounts", entry.getTableName());
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indices = entry.getDuplicatedIndices();
                    assertEquals(2, indices.size());
                    assertThat(indices.stream()
                                    .map(IndexWithSize::getIndexName)
                                    .collect(Collectors.toList()),
                            containsInAnyOrder("accounts_account_number_key", "i_accounts_account_number"));
                });
    }

    @Test
    void getIntersectedIndicesOnEmptyDataBase() {
        final var intersectedIndices = indexMaintenance.getIntersectedIndices();
        assertNotNull(intersectedIndices);
        assertEquals(0, intersectedIndices.size());
    }

    @Test
    void getIntersectedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var intersectedIndices = indexMaintenance.getIntersectedIndices();
                    assertNotNull(intersectedIndices);
                    assertEquals(0, intersectedIndices.size());
                });
    }

    @Test
    void getIntersectedIndicesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var intersectedIndices = indexMaintenance.getIntersectedIndices();
                    assertNotNull(intersectedIndices);
                    assertEquals(1, intersectedIndices.size());
                    final var entry = intersectedIndices.get(0);
                    assertEquals("clients", entry.getTableName());
                    assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
                    final var indices = entry.getDuplicatedIndices();
                    assertEquals(2, indices.size());
                    assertThat(indices.stream()
                                    .map(IndexWithSize::getIndexName)
                                    .collect(Collectors.toList()),
                            containsInAnyOrder("i_clients_last_first", "i_clients_last_name"));
                });
    }

    @Test
    void getPotentiallyUnusedIndicesOnEmptyDataBase() {
        final var unusedIndices = indexMaintenance.getPotentiallyUnusedIndices();
        assertNotNull(unusedIndices);
        assertEquals(0, unusedIndices.size());
    }

    @Test
    void getPotentiallyUnusedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var unusedIndices = indexMaintenance.getPotentiallyUnusedIndices();
                    assertNotNull(unusedIndices);
                    assertEquals(0, unusedIndices.size());
                });
    }

    @Test
    void getPotentiallyUnusedIndicesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createDuplicatedIndex();
                },
                () -> {
                    final var unusedIndices = indexMaintenance.getPotentiallyUnusedIndices();
                    assertNotNull(unusedIndices);
                    assertThat(unusedIndices.size(), equalTo(3));
                    final var names = unusedIndices.stream().map(UnusedIndex::getIndexName).collect(toSet());
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
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTables,
                () -> {
                    final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
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
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateOnlyTablesAndReferences();
                    databasePopulator.createNotSuitableIndexForForeignKey();
                },
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
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateOnlyTablesAndReferences();
                    databasePopulator.createSuitableIndexForForeignKey();
                },
                () -> {
                    var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndicesOnEmptyDataBase() {
        final var tables = indexMaintenance.getTablesWithMissingIndices();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithMissingIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var tables = indexMaintenance.getTablesWithMissingIndices();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @Test
    void getTablesWithMissingIndicesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.tryToFindAccountByClientId(101);
                },
                () -> {
                    var tables = indexMaintenance.getTablesWithMissingIndices();
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
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var tables = indexMaintenance.getTablesWithoutPrimaryKey();
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
                    var tables = indexMaintenance.getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("bad_clients", table.getTableName());
                });
    }

    @Test
    void getIndicesWithNullValuesOnEmptyDataBase() {
        final var indices = indexMaintenance.getIndicesWithNullValues();
        assertNotNull(indices);
        assertEquals(0, indices.size());
    }

    @Test
    void getIndicesWithNullValuesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var indices = indexMaintenance.getIndicesWithNullValues();
                    assertNotNull(indices);
                    assertEquals(0, indices.size());
                });
    }

    @Test
    void getIndicesWithNullValuesOnDatabaseWithThem() throws SQLException {
        executeTestOnDatabase(databasePopulator -> {
                    databasePopulator.populateWithDataAndReferences();
                    databasePopulator.createIndexWithNulls();
                },
                () -> {
                    final var indices = indexMaintenance.getIndicesWithNullValues();
                    assertNotNull(indices);
                    assertEquals(1, indices.size());
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
