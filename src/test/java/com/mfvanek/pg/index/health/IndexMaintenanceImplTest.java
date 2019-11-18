/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.utils.DatabasePopulator;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
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
        final var invalidIndexes = indexMaintenance.getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @Test
    void getInvalidIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var invalidIndexes = indexMaintenance.getInvalidIndexes();
            assertNotNull(invalidIndexes);
            assertEquals(0, invalidIndexes.size());
        }
    }

    @Test
    void getInvalidIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createInvalidIndex();

            final var invalidIndexes = indexMaintenance.getInvalidIndexes();
            assertNotNull(invalidIndexes);
            assertEquals(1, invalidIndexes.size());
            final var index = invalidIndexes.get(0);
            assertEquals("clients", index.getTableName());
            assertEquals("i_clients_last_name_first_name", index.getIndexName());
        }
    }

    @Test
    void getDuplicatedIndexesOnEmptyDataBase() {
        final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithoutThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateOnlyTablesAndReferences();

            final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
            assertNotNull(duplicatedIndexes);
            assertEquals(0, duplicatedIndexes.size());
        }
    }

    @Test
    void getDuplicatedIndexesOnDatabaseWithThem() throws SQLException {
        try (DatabasePopulator databasePopulator = new DatabasePopulator(embeddedPostgres.getTestDatabase())) {
            databasePopulator.populateWithDataAndReferences();
            databasePopulator.createDuplicatedIndex();

            final var duplicatedIndexes = indexMaintenance.getDuplicatedIndexes();
            assertNotNull(duplicatedIndexes);
            assertEquals(1, duplicatedIndexes.size());
            final var entry = duplicatedIndexes.get(0);
            assertEquals("accounts", entry.getTableName());
            assertThat(entry.getTotalSize(), greaterThanOrEqualTo(1L));
            final var indexes = entry.getIndexNames();
            assertEquals(2, indexes.size());
            assertLinesMatch(List.of("accounts_account_number_key", "i_accounts_account_number"), indexes);
        }
    }
}
