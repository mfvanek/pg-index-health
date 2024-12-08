/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.index.IndexWithSize;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipSmallIndexesPredicateTest {

    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipSmallIndexesPredicate.of(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("thresholdInBytes cannot be less than zero");
    }

    @Test
    void shouldNotCastObjectsWhenThresholdIsZero() {
        final IndexWithSize mockIndex = Mockito.mock(IndexWithSize.class);
        assertThat(SkipSmallIndexesPredicate.of(0L))
            .accepts(mockIndex);
        Mockito.verify(mockIndex, Mockito.never()).getIndexSizeInBytes();
    }

    @Test
    void shouldWork() {
        assertThat(SkipSmallIndexesPredicate.of(10L))
            .accepts(Table.of("t"))
            .accepts(IndexWithSize.of("t", "i", 10L))
            .accepts(IndexWithSize.of("t", "i", 11L))
            .accepts(SequenceState.of("s1", "int", 80.0))
            .rejects(IndexWithSize.of("t2", "i2", 9L));
    }
}
