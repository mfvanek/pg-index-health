/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndexesHealth;
import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.ForeignKey;
import com.mfvanek.pg.model.Index;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.IndexWithSize;
import com.mfvanek.pg.model.MemoryUnit;
import com.mfvanek.pg.model.PgContext;
import com.mfvanek.pg.model.Table;
import com.mfvanek.pg.model.TableWithMissingIndex;
import com.mfvanek.pg.model.UnusedIndex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;

class SimpleHealthLoggerTest {

    private final IndexesHealth indexesHealthMock = Mockito.mock(IndexesHealth.class);

    @Test
    void logInvalidIndexes() {
        Mockito.when(indexesHealthMock.getInvalidIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        Index.of("t1", "i1"),
                        Index.of("t1", "i2"),
                        Index.of("t2", "i3")
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, Exclusions.empty());
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.INVALID_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("invalid_indexes\t3"));
    }

    @Test
    void applyDuplicatedExclusions() {
        final var exclusions = Exclusions.builder()
                .withDuplicatedIndexesExclusions("i1, i3, ,,,")
                .build();
        Mockito.when(indexesHealthMock.getDuplicatedIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t1", "i1", 1L),
                                IndexWithSize.of("t1", "i2", 2L)
                        )),
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t2", "i3", 3L),
                                IndexWithSize.of("t2", "i4", 4L)
                        )),
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t3", "i5", 5L),
                                IndexWithSize.of("t3", "i6", 6L)
                        ))
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.DUPLICATED_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("duplicated_indexes\t1"));
    }

    @Test
    void applyUnusedExclusions() {
        final var exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.UNUSED_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("unused_indexes\t2"));
    }

    @Test
    void applyIntersectedIndexesExclusions() {
        final var exclusions = Exclusions.builder()
                .withIntersectedIndexesExclusions("i4,,  , i6")
                .build();
        Mockito.when(indexesHealthMock.getIntersectedIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t1", "i1", 1L),
                                IndexWithSize.of("t1", "i2", 2L)
                        )),
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t2", "i3", 3L),
                                IndexWithSize.of("t2", "i4", 4L)
                        )),
                        DuplicatedIndexes.of(List.of(
                                IndexWithSize.of("t3", "i5", 5L),
                                IndexWithSize.of("t3", "i6", 6L)
                        ))
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.INTERSECTED_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("intersected_indexes\t1"));
    }

    @Test
    void applyUnusedExclusionsWithSize() {
        final var exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(2L)
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.UNUSED_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("unused_indexes\t1"));
    }

    @Test
    void applyUnusedExclusionsWithSizeInMegabytes() {
        final var exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .withIndexSizeThreshold(1, MemoryUnit.MB)
                .build();
        Mockito.when(indexesHealthMock.getUnusedIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        UnusedIndex.of("t1", "i1", 1_048_576L, 1L),
                        UnusedIndex.of("t1", "i2", 1_048_575L, 2L),
                        UnusedIndex.of("t2", "i3", 1_048_574L, 3L),
                        UnusedIndex.of("t2", "i4", 1_048_573L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.UNUSED_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("unused_indexes\t1"));
    }

    @Test
    void logForeignKeysNotCoveredWithIndex() {
        Mockito.when(indexesHealthMock.getForeignKeysNotCoveredWithIndex(any(PgContext.class)))
                .thenReturn(List.of(
                        ForeignKey.of("t1", "c1", List.of("f1")),
                        ForeignKey.of("t1", "c2", List.of("f2")),
                        ForeignKey.of("t2", "c3", List.of("f3", "f4"))
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, Exclusions.empty());
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.FOREIGN_KEYS.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("foreign_keys_without_index\t3"));
    }

    @Test
    void applyTablesWithMissingIndexesExclusions() {
        final var exclusions = Exclusions.builder()
                .withTablesWithMissingIndexesExclusions("t1,  ,,  , t3   ")
                .build();
        Mockito.when(indexesHealthMock.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        TableWithMissingIndex.of("t1", 0L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 0L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 0L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 0L, 404L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("tables_with_missing_indexes\t2"));
    }

    @Test
    void applyTablesWithMissingIndexesExclusionsBySize() {
        final var exclusions = Exclusions.builder()
                .withTableSizeThreshold(98L)
                .build();
        Mockito.when(indexesHealthMock.getTablesWithMissingIndexes(any(PgContext.class)))
                .thenReturn(List.of(
                        TableWithMissingIndex.of("t1", 97L, 101L, 1L),
                        TableWithMissingIndex.of("t2", 98L, 202L, 2L),
                        TableWithMissingIndex.of("t3", 99L, 303L, 3L),
                        TableWithMissingIndex.of("t4", 100L, 404L, 4L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("tables_with_missing_indexes\t3"));
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusions() {
        final var exclusions = Exclusions.builder()
                .withTablesWithoutPrimaryKeyExclusions("  ,,   ,   , t4, t6")
                .build();
        Mockito.when(indexesHealthMock.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(List.of(
                        Table.of("t1", 0L),
                        Table.of("t2", 0L),
                        Table.of("t3", 0L),
                        Table.of("t4", 0L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.TABLES_WITHOUT_PK.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("tables_without_primary_key\t3"));
    }

    @Test
    void applyTablesWithoutPrimaryKeyExclusionsBySize() {
        final var exclusions = Exclusions.builder()
                .withTableSizeThreshold(100L)
                .build();
        Mockito.when(indexesHealthMock.getTablesWithoutPrimaryKey(any(PgContext.class)))
                .thenReturn(List.of(
                        Table.of("t1", 10L),
                        Table.of("t2", 50L),
                        Table.of("t3", 100L),
                        Table.of("t4", 200L)
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.TABLES_WITHOUT_PK.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("tables_without_primary_key\t2"));
    }

    @Test
    void applyIndexesWithNullValuesExclusions() {
        final var exclusions = Exclusions.builder()
                .withIndexesWithNullValuesExclusions("i2, i5, , ,,  ")
                .build();
        Mockito.when(indexesHealthMock.getIndexesWithNullValues(any(PgContext.class)))
                .thenReturn(List.of(
                        IndexWithNulls.of("t1", "i1", 1L, "f1"),
                        IndexWithNulls.of("t1", "i2", 2L, "f2"),
                        IndexWithNulls.of("t2", "i3", 3L, "f3"),
                        IndexWithNulls.of("t2", "i4", 4L, "f4")
                ));
        final IndexesHealthLogger logger = new SimpleHealthLogger(indexesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.INDEXES_WITH_NULLS.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("indexes_with_null_values\t3"));
    }
}
