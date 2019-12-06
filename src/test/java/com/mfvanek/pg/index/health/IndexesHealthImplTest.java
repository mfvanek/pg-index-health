/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.HighAvailabilityPgConnection;
import com.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import com.mfvanek.pg.connection.PgConnectionImpl;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactoryImpl;
import com.mfvanek.pg.utils.DatabasePopulator;
import com.mfvanek.pg.utils.TestExecutor;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// TODO Add tests for non zero cases
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
    void getInvalidIndicesOnEmptyDatabase() {
        final var invalidIndices = indexesHealth.getInvalidIndexes();
        assertNotNull(invalidIndices);
        assertEquals(0, invalidIndices.size());
    }

    @Test
    void getInvalidIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var invalidIndices = indexesHealth.getInvalidIndexes();
                    assertNotNull(invalidIndices);
                    assertEquals(0, invalidIndices.size());
                });
    }

    @Test
    void getDuplicatedIndicesOnEmptyDatabase() {
        final var duplicatedIndices = indexesHealth.getDuplicatedIndexes();
        assertNotNull(duplicatedIndices);
        assertEquals(0, duplicatedIndices.size());
    }

    @Test
    void getDuplicatedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var duplicatedIndices = indexesHealth.getDuplicatedIndexes();
                    assertNotNull(duplicatedIndices);
                    assertEquals(0, duplicatedIndices.size());
                });
    }

    @Test
    void getIntersectedIndicesOnEmptyDatabase() {
        final var intersectedIndices = indexesHealth.getIntersectedIndexes();
        assertNotNull(intersectedIndices);
        assertEquals(0, intersectedIndices.size());
    }

    @Test
    void getIntersectedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var intersectedIndices = indexesHealth.getIntersectedIndexes();
                    assertNotNull(intersectedIndices);
                    assertEquals(0, intersectedIndices.size());
                });
    }

    @Test
    void getUnusedIndicesOnEmptyDatabase() {
        final var unusedIndices = indexesHealth.getUnusedIndexes();
        assertNotNull(unusedIndices);
        assertEquals(0, unusedIndices.size());
    }

    @Test
    void getUnusedIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateOnlyTablesAndReferences,
                () -> {
                    final var unusedIndices = indexesHealth.getUnusedIndexes();
                    assertNotNull(unusedIndices);
                    assertEquals(0, unusedIndices.size());
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
    void getTablesWithMissingIndicesOnEmptyDatabase() {
        final var tables = indexesHealth.getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithMissingIndicesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var tables = indexesHealth.getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
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
    void getIndicesWithNullValuesOnEmptyDatabase() {
        final var indices = indexesHealth.getIndexesWithNullValues();
        assertNotNull(indices);
        assertEquals(0, indices.size());
    }

    @Test
    void getIndicesWithNullValuesOnDatabaseWithoutThem() throws SQLException {
        executeTestOnDatabase(DatabasePopulator::populateWithDataAndReferences,
                () -> {
                    final var indices = indexesHealth.getIndexesWithNullValues();
                    assertNotNull(indices);
                    assertEquals(0, indices.size());
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
