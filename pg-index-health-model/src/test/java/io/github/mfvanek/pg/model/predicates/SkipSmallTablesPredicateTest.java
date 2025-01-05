/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipSmallTablesPredicateTest {

    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipSmallTablesPredicate.of(-1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("thresholdInBytes cannot be less than zero");
    }

    @Test
    void shouldNotCastObjectsWhenThresholdIsZero() {
        final Table mockTable = Mockito.mock(Table.class);
        assertThat(SkipSmallTablesPredicate.of(0L))
            .accepts(mockTable);
        Mockito.verify(mockTable, Mockito.never()).getTableSizeInBytes();
    }

    @Test
    void shouldWork() {
        assertThat(SkipSmallTablesPredicate.of(10L))
            .accepts(Table.of("t", 10L))
            .accepts(Table.of("t", 11L))
            .accepts(SequenceState.of("s1", "int", 80.0))
            .rejects(TableWithBloat.of(Table.of("t2", 1L), 1L, 10.0))
            .rejects(Table.of("t2", 1L));
    }
}
