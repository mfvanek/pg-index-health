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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

abstract class IndexMaintenanceImplTestBase extends DatabaseAwareTestBase {

    private static final long AMOUNT_OF_TRIES = 101L;

    private final PgConnection pgConnection;

    IndexMaintenanceImplTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
        this.pgConnection = PgConnectionImpl.ofMaster(dataSource);
    }

    @Test
    void getInvalidIndexesOnEmptyDataBase() {
        final var invalidIndexes = maintenance().getInvalidIndexes();
        assertNotNull(invalidIndexes);
        assertEquals(0, invalidIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().populate(),
                () -> {
                    final var invalidIndexes = maintenance(schemaName).getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(0, invalidIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().withInvalidIndex().populate(),
                () -> {
                    final var invalidIndexes = maintenance(schemaName).getInvalidIndexes();
                    assertNotNull(invalidIndexes);
                    assertEquals(1, invalidIndexes.size());
                    final var index = invalidIndexes.get(0);
                    assertEquals("clients", index.getTableName());
                    assertEquals("i_clients_last_name_first_name", index.getIndexName());
                });
    }

    @Test
    void getDuplicatedIndexesOnEmptyDataBase() {
        final var duplicatedIndexes = maintenance().getDuplicatedIndexes();
        assertNotNull(duplicatedIndexes);
        assertEquals(0, duplicatedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp -> dbp.withSchema(schemaName).withReferences().populate(),
                () -> {
                    final var duplicatedIndexes = maintenance(schemaName).getDuplicatedIndexes();
                    assertNotNull(duplicatedIndexes);
                    assertEquals(0, duplicatedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withDuplicatedIndex().populate(),
                () -> {
                    final var duplicatedIndexes = maintenance(schemaName).getDuplicatedIndexes();
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
        final var intersectedIndexes = maintenance().getIntersectedIndexes();
        assertNotNull(intersectedIndexes);
        assertEquals(0, intersectedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp -> dbp.withSchema(schemaName).withReferences().populate(),
                () -> {
                    final var intersectedIndexes = maintenance(schemaName).getIntersectedIndexes();
                    assertNotNull(intersectedIndexes);
                    assertEquals(0, intersectedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().withDuplicatedIndex().populate(),
                () -> {
                    final var intersectedIndexes = maintenance(schemaName).getIntersectedIndexes();
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
                                "i_clients_last_first", "i_clients_last_name"));
                    } else {
                        assertThat(names, containsInAnyOrder(
                                schemaName + ".i_clients_last_first", schemaName + ".i_clients_last_name"));
                    }
                });
    }

    @Test
    void getPotentiallyUnusedIndexesOnEmptyDataBase() {
        final var unusedIndexes = maintenance().getPotentiallyUnusedIndexes();
        assertNotNull(unusedIndexes);
        assertEquals(0, unusedIndexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getPotentiallyUnusedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp -> dbp.withSchema(schemaName).withReferences().populate(),
                () -> {
                    final var unusedIndexes = maintenance(schemaName).getPotentiallyUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertEquals(0, unusedIndexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getPotentiallyUnusedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().withDuplicatedIndex().populate(),
                () -> {
                    final var unusedIndexes = maintenance(schemaName).getPotentiallyUnusedIndexes();
                    assertNotNull(unusedIndexes);
                    assertThat(unusedIndexes.size(), equalTo(3));
                    final var names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
                    assertThat(names, containsInAnyOrder("i_clients_last_first", "i_clients_last_name", "i_accounts_account_number"));
                });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDataBase() {
        final var foreignKeys = maintenance().getForeignKeysNotCoveredWithIndex();
        assertNotNull(foreignKeys);
        assertEquals(0, foreignKeys.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp -> dbp.withSchema(schemaName).populate(),
                () -> {
                    final var foreignKeys = maintenance(schemaName).getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp -> dbp.withSchema(schemaName).withReferences().populate(),
                () -> {
                    var foreignKeys = maintenance(schemaName).getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withNonSuitableIndex().populate(),
                () -> {
                    var foreignKeys = maintenance(schemaName).getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(1, foreignKeys.size());
                    final var foreignKey = foreignKeys.get(0);
                    assertEquals("accounts", foreignKey.getTableName());
                    assertThat(foreignKey.getColumnsInConstraint(), containsInAnyOrder("client_id"));
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withSuitableIndex().populate(),
                () -> {
                    var foreignKeys = maintenance(schemaName).getForeignKeysNotCoveredWithIndex();
                    assertNotNull(foreignKeys);
                    assertEquals(0, foreignKeys.size());
                });
    }

    @Test
    void getTablesWithMissingIndexesOnEmptyDataBase() {
        final var tables = maintenance().getTablesWithMissingIndexes();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().populate(),
                () -> {
                    final var tables = maintenance(schemaName).getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithMissingIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp -> {
                    dbp.withSchema(schemaName).withReferences().withData().populate();
                    dbp.tryToFindAccountByClientId(AMOUNT_OF_TRIES);
                },
                () -> {
                    var tables = maintenance(schemaName).getTablesWithMissingIndexes();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("accounts", table.getTableName());
                    assertThat(table.getSeqScans(), greaterThanOrEqualTo(AMOUNT_OF_TRIES));
                    assertEquals(0, table.getIndexScans());
                });
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDataBase() {
        final var tables = maintenance().getTablesWithoutPrimaryKey();
        assertNotNull(tables);
        assertEquals(0, tables.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().populate(),
                () -> {
                    final var tables = maintenance(schemaName).getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(0, tables.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getTablesWithoutPrimaryKeyOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().withTableWithoutPrimaryKey().populate(),
                () -> {
                    var tables = maintenance(schemaName).getTablesWithoutPrimaryKey();
                    assertNotNull(tables);
                    assertEquals(1, tables.size());
                    var table = tables.get(0);
                    assertEquals("bad_clients", table.getTableName());
                });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDataBase() {
        final var indexes = maintenance().getIndexesWithNullValues();
        assertNotNull(indexes);
        assertEquals(0, indexes.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().populate(),
                () -> {
                    final var indexes = maintenance(schemaName).getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(0, indexes.size());
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(dbp ->
                        dbp.withSchema(schemaName).withReferences().withData().withNullValuesInIndex().populate(),
                () -> {
                    final var indexes = maintenance(schemaName).getIndexesWithNullValues();
                    assertNotNull(indexes);
                    assertEquals(1, indexes.size());
                });
    }

    @Nonnull
    private IndexMaintenance maintenance() {
        return new IndexMaintenanceImpl(pgConnection, PgContext.ofPublic());
    }

    @Nonnull
    private IndexMaintenance maintenance(@Nonnull final String schemaName) {
        return new IndexMaintenanceImpl(pgConnection, PgContext.of(schemaName));
    }
}
