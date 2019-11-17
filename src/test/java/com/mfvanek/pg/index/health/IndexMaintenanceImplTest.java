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
}
