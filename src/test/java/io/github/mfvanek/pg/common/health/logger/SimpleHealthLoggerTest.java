/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.health.DatabaseHealth;
import io.github.mfvanek.pg.common.health.DatabaseHealthFactory;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static io.github.mfvanek.pg.utils.HealthLoggerAssertions.assertContainsKey;
import static org.mockito.ArgumentMatchers.any;

class SimpleHealthLoggerTest {

    private final ConnectionCredentials credentials = Mockito.mock(ConnectionCredentials.class);
    private final DatabaseHealth databaseHealth = Mockito.mock(DatabaseHealth.class);
    private final DatabaseHealthFactory databaseHealthFactory = Mockito.mock(DatabaseHealthFactory.class);
    private final HighAvailabilityPgConnectionFactory connectionFactory = Mockito.mock(HighAvailabilityPgConnectionFactory.class);
    private final HealthLogger logger = new SimpleHealthLogger(credentials, connectionFactory, databaseHealthFactory);

    @BeforeEach
    void setUp() {
        Mockito.when(databaseHealthFactory.of(any())).thenReturn(databaseHealth);
    }

    @Test
    void logInvalidIndexes() {
        Mockito.when(databaseHealth.getInvalidIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Index.of("t1", "i1"),
                        Index.of("t1", "i2"),
                        Index.of("t2", "i3")
                ));
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes\t3");
    }

    @Test
    void applyDuplicatedExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withDuplicatedIndexesExclusions("i1, i3, ,,,")
                .build();
        Mockito.when(databaseHealth.getDuplicatedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t1", "i1", 1L),
                                IndexWithSize.of("t1", "i2", 2L)
                        )),
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t2", "i3", 3L),
                                IndexWithSize.of("t2", "i4", 4L)
                        )),
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t3", "i5", 5L),
                                IndexWithSize.of("t3", "i6", 6L)
                        ))
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.DUPLICATED_INDEXES, "duplicated_indexes\t1");
    }

    @Test
    void applyUnusedExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .build();
        Mockito.when(databaseHealth.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t2");
    }

    @Test
    void applyIntersectedIndexesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withIntersectedIndexesExclusions("i4,,  , i6")
                .build();
        Mockito.when(databaseHealth.getIntersectedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t1", "i1", 1L),
                                IndexWithSize.of("t1", "i2", 2L)
                        )),
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t2", "i3", 3L),
                                IndexWithSize.of("t2", "i4", 4L)
                        )),
                        DuplicatedIndexes.of(Arrays.asList(
                                IndexWithSize.of("t3", "i5", 5L),
                                IndexWithSize.of("t3", "i6", 6L)
                        ))
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.INTERSECTED_INDEXES, "intersected_indexes\t1");
    }

    @Test
    void applyUnusedExclusionsWithSize() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(2L)
                .build();
        Mockito.when(databaseHealth.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t1");
    }

    @Test
    void applyUnusedExclusionsWithSizeInMegabytes() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(1, MemoryUnit.MB)
                .build();
        Mockito.when(databaseHealth.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1_048_576L, 1L),
                        UnusedIndex.of("t1", "i2", 1_048_575L, 2L),
                        UnusedIndex.of("t2", "i3", 1_048_574L, 3L),
                        UnusedIndex.of("t2", "i4", 1_048_573L, 4L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t1");
    }

    @Test
    void logForeignKeysNotCoveredWithIndex() {
        Mockito.when(databaseHealth.getForeignKeysNotCoveredWithIndex(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        ForeignKey.ofColumn("t1", "c1", "f1"),
                        ForeignKey.ofColumn("t1", "c2", "f2"),
                        ForeignKey.of("t2", "c3", Arrays.asList("f3", "f4"))
                ));
        final List<String> logs = logger.logAll(Exclusions.empty());
        assertContainsKey(logs, SimpleLoggingKey.FOREIGN_KEYS, "foreign_keys_without_index\t3");
    }

    @Test
    void applyTablesWithMissingIndexesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withTablesWithMissingIndexesExclusions("t1,  ,,  , t3   ")
                .build();
        Mockito.when(databaseHealth.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithMissingIndex.of("t1", 0L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 0L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 0L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 0L, 404L, 4L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES, "tables_with_missing_indexes\t2");
    }

    @Test
    void applyTablesWithMissingIndexesExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(98L)
                .build();
        Mockito.when(databaseHealth.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithMissingIndex.of("t1", 97L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 98L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 99L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 100L, 404L, 4L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES, "tables_with_missing_indexes\t3");
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withTablesWithoutPrimaryKeyExclusions("  ,,   ,   , t4, t6")
                .build();
        Mockito.when(databaseHealth.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Table.of("t1", 0L),
                        Table.of("t2", 0L),
                        Table.of("t3", 0L),
                        Table.of("t4", 0L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t3");
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(100L)
                .build();
        Mockito.when(databaseHealth.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Table.of("t1", 10L),
                        Table.of("t2", 50L),
                        Table.of("t3", 100L),
                        Table.of("t4", 200L)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t2");
    }

    @Test
    void applyIndexesWithNullValuesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withIndexesWithNullValuesExclusions("i2, i5, , ,,  ")
                .build();
        Mockito.when(databaseHealth.getIndexesWithNullValues(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        IndexWithNulls.of("t1", "i1", 1L, "f1"),
                        IndexWithNulls.of("t1", "i2", 2L, "f2"),
                        IndexWithNulls.of("t2", "i3", 3L, "f3"),
                        IndexWithNulls.of("t2", "i4", 4L, "f4")
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.INDEXES_WITH_NULLS, "indexes_with_null_values\t3");
    }

    @Test
    void applyIndexesBloatExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withIndexSizeThreshold(20L)
                .withIndexBloatSizeThreshold(10L)
                .build();
        Mockito.when(databaseHealth.getIndexesWithBloat(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        IndexWithBloat.of("t1", "i1", 15L, 10L, 67),
                        IndexWithBloat.of("t1", "i2", 20L, 9L, 45),
                        IndexWithBloat.of("t2", "i3", 30L, 10L, 33),
                        IndexWithBloat.of("t2", "i4", 40L, 20L, 50)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.INDEXES_BLOAT, "indexes_bloat\t2");
    }

    @Test
    void applyIndexesBloatExclusionsByPercentage() {
        final Exclusions exclusions = Exclusions.builder()
                .withIndexSizeThreshold(16)
                .withIndexBloatPercentageThreshold(51)
                .build();
        Mockito.when(databaseHealth.getIndexesWithBloat(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        IndexWithBloat.of("t1", "i1", 15L, 10L, 67),
                        IndexWithBloat.of("t1", "i2", 20L, 9L, 45),
                        IndexWithBloat.of("t2", "i3", 30L, 10L, 33),
                        IndexWithBloat.of("t2", "i4", 40L, 21L, 52)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.INDEXES_BLOAT, "indexes_bloat\t1");
    }

    @Test
    void applyTablesBloatExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(21L)
                .withTableBloatSizeThreshold(11L)
                .build();
        Mockito.when(databaseHealth.getTablesWithBloat(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithBloat.of("t1", 15L, 11L, 73),
                        TableWithBloat.of("t2", 20L, 9L, 45),
                        TableWithBloat.of("t3", 30L, 10L, 33),
                        TableWithBloat.of("t4", 40L, 20L, 50)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_BLOAT, "tables_bloat\t1");
    }

    @Test
    void applyTablesBloatExclusionsByPercentage() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(16L)
                .withTableBloatPercentageThreshold(35)
                .build();
        Mockito.when(databaseHealth.getTablesWithBloat(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithBloat.of("t1", 15L, 11L, 73),
                        TableWithBloat.of("t2", 20L, 9L, 45),
                        TableWithBloat.of("t3", 30L, 10L, 33),
                        TableWithBloat.of("t4", 40L, 20L, 50)
                ));
        final List<String> logs = logger.logAll(exclusions);
        assertContainsKey(logs, SimpleLoggingKey.TABLES_BLOAT, "tables_bloat\t2");
    }
}
