/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndicesHealth;
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
                .withUnusedIndicesExclusions("i2, i3, , ,,  ")
                .build();
        final var indicesHealthMock = Mockito.mock(IndicesHealth.class);
        Mockito.when(indicesHealthMock.getUnusedIndices())
                .thenReturn(List.of(
                        UnusedIndex.of("t1", "i1", 1L, 1L),
                        UnusedIndex.of("t1", "i2", 2L, 2L),
                        UnusedIndex.of("t2", "i3", 3L, 3L),
                        UnusedIndex.of("t2", "i4", 4L, 4L)
                ));
        final IndicesHealthLogger logger = new SimpleHealthLogger(indicesHealthMock, exclusions);
        final var logs = logger.logAll();
        final var logStr = logs.stream()
                .filter(l -> l.contains(SimpleLoggingKey.UNUSED_INDICES.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString("unused_indices\t2"));
    }
}
