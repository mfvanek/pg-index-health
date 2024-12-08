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

import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipBloatUnderThresholdPredicateTest {

    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipBloatUnderThresholdPredicate.of(-1L, -1.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sizeThresholdInBytes cannot be less than zero");

        assertThatThrownBy(() -> SkipBloatUnderThresholdPredicate.of(0L, -1.0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");

        assertThatThrownBy(() -> SkipBloatUnderThresholdPredicate.of(0L, 100.1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("percentageThreshold should be in the range from 0.0 to 100.0 inclusive");
    }

    @Test
    void shouldNotCastObjectsWhenThresholdIsZero() {
        final TableWithBloat mockBloat = Mockito.mock(TableWithBloat.class);
        assertThat(SkipBloatUnderThresholdPredicate.of(0L, 0.0))
            .accepts(mockBloat);
        Mockito.verify(mockBloat, Mockito.never()).getBloatPercentage();
        Mockito.verify(mockBloat, Mockito.never()).getBloatSizeInBytes();
    }

    @Test
    void shouldWork() {
        assertThat(SkipBloatUnderThresholdPredicate.of(100L, 10.0))
            .accepts(Table.of("t"))
            .accepts(SequenceState.of("s1", "int", 80.0))
            .rejects(TableWithBloat.of(Table.of("t2", 1L), 1L, 10.0))
            .rejects(TableWithBloat.of(Table.of("t2", 1L), 1L, 11.0))
            .accepts(TableWithBloat.of(Table.of("t2", 100L), 100L, 10.0))
            .accepts(TableWithBloat.of(Table.of("t2", 200L), 101L, 10.0))
            .rejects(IndexWithBloat.of("t2", "i2", 1L, 1L, 10.0))
            .rejects(IndexWithBloat.of("t2", "i2", 1L, 1L, 11.0))
            .accepts(IndexWithBloat.of("t2", "i2", 100L, 100L, 10.0))
            .accepts(IndexWithBloat.of("t2", "i2", 200L, 101L, 10.0));

        assertThat(SkipBloatUnderThresholdPredicate.of(0L, 1.0))
            .rejects(TableWithBloat.of(Table.of("t2", 1L), 0L, 0.1))
            .accepts(TableWithBloat.of(Table.of("t2", 100L), 1L, 1.0));

        assertThat(SkipBloatUnderThresholdPredicate.of(1L, 0.0))
            .rejects(TableWithBloat.of(Table.of("t2", 1L), 0L, 0.1))
            .accepts(TableWithBloat.of(Table.of("t2", 100L), 1L, 0.0));
    }
}
