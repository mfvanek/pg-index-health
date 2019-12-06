/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndexesHealth;
import com.mfvanek.pg.model.IndexWithNulls;
import com.mfvanek.pg.model.UnusedIndex;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class SimpleHealthLoggerTest {

    @Test
    void applyUnusedExclusions() {
        final var exclusions = Exclusions.builder()
                .withUnusedIndexesExclusions("i2, i3, , ,,  ")
                .build();
        final var indexesHealthMock = Mockito.mock(IndexesHealth.class);
        Mockito.when(indexesHealthMock.getUnusedIndexes())
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
    void applyIndexesWithNullValuesExclusions() {
        final var exclusions = Exclusions.builder()
                .withIndexesWithNullValuesExclusions("i2, i5, , ,,  ")
                .build();
        final var indexesHealthMock = Mockito.mock(IndexesHealth.class);
        Mockito.when(indexesHealthMock.getIndexesWithNullValues())
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
