/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.health.logger;

import io.github.mfvanek.pg.index.health.IndexesHealth;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.IndexWithSize;
import io.github.mfvanek.pg.model.MemoryUnit;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import io.github.mfvanek.pg.utils.HealthLoggerAssertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;

class SimpleHealthLoggerTest {

    private final IndexesHealth indexesHealthMock = Mockito.mock(IndexesHealth.class);

    @Test
    void logInvalidIndexes() {
        Mockito.when(indexesHealthMock.getInvalidIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Index.of("t1", "i1"),
                        Index.of("t1", "i2"),
                        Index.of("t2", "i3")
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(Exclusions.empty());
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.INVALID_INDEXES, "invalid_indexes\t3");
    }

    @Test
    void applyDuplicatedExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withDuplicatedIndexesExclusions("i1, i3, ,,,")
                .build();
        Mockito.when(indexesHealthMock.getDuplicatedIndexes(any(PgContext.class)))
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
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.DUPLICATED_INDEXES, "duplicated_indexes\t1");
    }

    @Test
    void applyUnusedExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t2");
    }

    @Test
    void applyIntersectedIndexesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withIntersectedIndexesExclusions("i4,,  , i6")
                .build();
        Mockito.when(indexesHealthMock.getIntersectedIndexes(any(PgContext.class)))
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
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.INTERSECTED_INDEXES, "intersected_indexes\t1");
    }

    @Test
    void applyUnusedExclusionsWithSize() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(2L)
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t1");
    }

    @Test
    void applyUnusedExclusionsWithSizeInMegabytes() {
        final Exclusions exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(1, MemoryUnit.MB)
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        UnusedIndex.of("t1", "i1", 1_048_576L, 1L),
                        UnusedIndex.of("t1", "i2", 1_048_575L, 2L),
                        UnusedIndex.of("t2", "i3", 1_048_574L, 3L),
                        UnusedIndex.of("t2", "i4", 1_048_573L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.UNUSED_INDEXES, "unused_indexes\t1");
    }

    @Test
    void logForeignKeysNotCoveredWithIndex() {
        Mockito.when(indexesHealthMock.getForeignKeysNotCoveredWithIndex(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        ForeignKey.of("t1", "c1", Collections.singletonList("f1")),
                        ForeignKey.of("t1", "c2", Collections.singletonList("f2")),
                        ForeignKey.of("t2", "c3", Arrays.asList("f3", "f4"))
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(Exclusions.empty());
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.FOREIGN_KEYS, "foreign_keys_without_index\t3");
    }

    @Test
    void applyTablesWithMissingIndexesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withTablesWithMissingIndexesExclusions("t1,  ,,  , t3   ")
                .build();
        Mockito.when(indexesHealthMock.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithMissingIndex.of("t1", 0L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 0L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 0L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 0L, 404L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES, "tables_with_missing_indexes\t2");
    }

    @Test
    void applyTablesWithMissingIndexesExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(98L)
                .build();
        Mockito.when(indexesHealthMock.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        TableWithMissingIndex.of("t1", 97L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 98L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 99L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 100L, 404L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES, "tables_with_missing_indexes\t3");
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withTablesWithoutPrimaryKeyExclusions("  ,,   ,   , t4, t6")
                .build();
        Mockito.when(indexesHealthMock.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Table.of("t1", 0L),
                        Table.of("t2", 0L),
                        Table.of("t3", 0L),
                        Table.of("t4", 0L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t3");
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusionsBySize() {
        final Exclusions exclusions = Exclusions.builder()
                .withTableSizeThreshold(100L)
                .build();
        Mockito.when(indexesHealthMock.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        Table.of("t1", 10L),
                        Table.of("t2", 50L),
                        Table.of("t3", 100L),
                        Table.of("t4", 200L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.TABLES_WITHOUT_PK, "tables_without_primary_key\t2");
    }

    @Test
    void applyIndexesWithNullValuesExclusions() {
        final Exclusions exclusions = Exclusions.builder()
                .withIndexesWithNullValuesExclusions("i2, i5, , ,,  ")
                .build();
        Mockito.when(indexesHealthMock.getIndexesWithNullValues(any(PgContext.class)))
                .thenReturn(Arrays.asList(
                        IndexWithNulls.of("t1", "i1", 1L, "f1"),
                        IndexWithNulls.of("t1", "i2", 2L, "f2"),
                        IndexWithNulls.of("t2", "i3", 3L, "f3"),
                        IndexWithNulls.of("t2", "i4", 4L, "f4")
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock);
        final List<String> logs = logger.logAll(exclusions);
        HealthLoggerAssertions.assertContainsKey(logs, SimpleLoggingKey.INDEXES_WITH_NULLS, "indexes_with_null_values\t3");
    }
}
