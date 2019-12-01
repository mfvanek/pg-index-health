/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import com.mfvanek.pg.connection.SimplePgConnection;
import com.mfvanek.pg.index.maintenance.IndexMaintenanceFactoryImpl;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IndicesHealthImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    private final IndicesHealth indicesHealth = new IndicesHealthImpl(SimplePgConnection.of(embeddedPostgres.getTestDatabase()),
            new IndexMaintenanceFactoryImpl());

    @Test
    void getInvalidIndicesOnEmptyDatabase() {
        final var invalidIndices = indicesHealth.getInvalidIndices();
        assertNotNull(invalidIndices);
        assertEquals(0, invalidIndices.size());
    }

    @Test
    void getDuplicatedIndicesOnEmptyDatabase() {
        final var duplicatedIndices = indicesHealth.getDuplicatedIndices();
        assertNotNull(duplicatedIndices);
        assertEquals(0, duplicatedIndices.size());
    }

    @Test
    void getIntersectedIndicesOnEmptyDatabase() {
        final var intersectedIndices = indicesHealth.getIntersectedIndices();
        assertNotNull(intersectedIndices);
        assertEquals(0, intersectedIndices.size());
    }

    @Test
    void getUnusedIndicesOnEmptyDatabase() {
        final var unusedIndices = indicesHealth.getUnusedIndices();
        assertNotNull(unusedIndices);
        assertEquals(0, unusedIndices.size());
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDatabase() {
        final var foreignKeys = indicesHealth.getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @Test
    void getTablesWithMissingIndicesOnEmptyDatabase() {
        final var tables = indicesHealth.getTablesWithMissingIndices();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        final var tables = indicesHealth.getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @Test
    void getIndicesWithNullValuesOnEmptyDatabase() {
        final var indices = indicesHealth.getIndicesWithNullValues();
        assertNotNull(indices);
        assertEquals(0, indices.size());
    }
}
