/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.model.IndexWithSize;
import com.mfvanek.pg.model.UnusedIndex;
import com.mfvanek.pg.utils.DatabasePopulator;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;
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
    void getInvalidIndexesOnEmptyDataBase() {
        final var invalidIndexes = indexMaintenance.getInvalidIndices();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @Test
    void getInvalidIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var invalidIndexes = indexMaintenance.getInvalidIndices();
            assertNotNull(invalidIndexes);
            assertEquals(0, invalidIndexes.size());
        }
    }

    @Test
    void getInvalidIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createInvalidIndex();

            final var invalidIndexes = indexMaintenance.getInvalidIndices();
            assertNotNull(invalidIndexes);
            assertEquals(1, invalidIndexes.size());
            final var index = invalidIndexes.get(0);
            assertEquals("clients", index.getTableName());
            assertEquals("i_clients_last_name_first_name", index.getIndexName());
        }
    }

    @Test
    void getDuplicatedIndexesOnEmptyDataBase() {
        final var duplicatedIndexes = indexMaintenance.getDuplicatedIndices();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var duplicatedIndexes = indexMaintenance.getDuplicatedIndices();
            assertNotNull(duplicatedIndexes);
            assertEquals(0, duplicatedIndexes.size());
        }
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createDuplicatedIndex();

            final var duplicatedIndexes = indexMaintenance.getDuplicatedIndices();
            assertNotNull(duplicatedIndexes);
            assertEquals(1, duplicatedIndexes.size());
            final var entry = duplicatedIndexes.get(0);
            assertEquals("accounts", entry.getTableName());
            assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
            final var indexes = entry.getDuplicatedIndices();
            assertEquals(2, indexes.size());
            assertThat(indexes.stream()
                            .map(IndexWithSize::getIndexName)
                            .collect(Collectors.toList()),
                    containsInAnyOrder("accounts_account_number_key", "i_accounts_account_number"));
        }
    }

    @Test
    void getIntersectedIndexesOnEmptyDataBase() {
        final var intersectedIndexes = indexMaintenance.getIntersectedIndices();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var intersectedIndexes = indexMaintenance.getIntersectedIndices();
            assertNotNull(intersectedIndexes);
            assertEquals(0, intersectedIndexes.size());
        }
    }

    @Test
    void getIntersectedIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createDuplicatedIndex();

            final var intersectedIndexes = indexMaintenance.getIntersectedIndices();
            assertNotNull(intersectedIndexes);
            assertEquals(1, intersectedIndexes.size());
            final var entry = intersectedIndexes.get(0);
            assertEquals("clients", entry.getTableName());
            assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
            final var indexes = entry.getDuplicatedIndices();
            assertEquals(2, indexes.size());
            assertThat(indexes.stream()
                            .map(IndexWithSize::getIndexName)
                            .collect(Collectors.toList()),
                    containsInAnyOrder("i_clients_last_first", "i_clients_last_name"));
        }
    }

    @Test
    void getPotentiallyUnusedIndexesOnEmptyDataBase() {
        final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndices();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @Test
    void getPotentiallyUnusedIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndices();
            assertNotNull(unusedIndexes);
            assertEquals(0, unusedIndexes.size());
        }
    }

    @Test
    void getPotentiallyUnusedIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createDuplicatedIndex();

            final var unusedIndexes = indexMaintenance.getPotentiallyUnusedIndices();
            assertNotNull(unusedIndexes);
            assertThat(unusedIndexes.size(), equalTo(2));
            final var names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
            assertThat(names, containsInAnyOrder("i_clients_last_first", "i_clients_last_name"));
        }
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDataBase() {
        final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTables();

            final var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
            assertNotNull(foreignKeys);
            assertEquals(0, foreignKeys.size());
        }
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            var foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
            assertNotNull(foreignKeys);
            assertEquals(1, foreignKeys.size());
            final var foreignKey = foreignKeys.get(0);
            assertEquals("accounts", foreignKey.getTableName());
            assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));

            databasePopulator.createNotSuitableIndexForForeignKey();
            foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
            assertNotNull(foreignKeys);
            assertEquals(1, foreignKeys.size());

            databasePopulator.createSuitableIndexForForeignKey();
            foreignKeys = indexMaintenance.getForeignKeysNotCoveredWithIndex();
            assertNotNull(foreignKeys);
            assertEquals(0, foreignKeys.size());
        }
    }
}
