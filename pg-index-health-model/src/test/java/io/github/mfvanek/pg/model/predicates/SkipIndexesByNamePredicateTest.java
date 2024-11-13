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

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipIndexesByNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawIndexNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawIndexNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawIndexNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofPublic(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofPublic();
        assertThatThrownBy(() -> SkipIndexesByNamePredicate.of(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawIndexNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName(ctx, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawIndexNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipIndexesByNamePredicate.ofName(ctx, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawIndexNameToSkip cannot be blank");
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final Index mockIndex = Mockito.mock(Index.class);
        assertThat(SkipIndexesByNamePredicate.ofPublic(List.of()))
            .accepts(mockIndex);
        Mockito.verify(mockIndex, Mockito.never()).getIndexName();
    }

    @Test
    void shouldWorkForSingleIndex() {
        assertThat(SkipIndexesByNamePredicate.ofName("i2"))
            .accepts(Table.of("t", 0L))
            .accepts(Index.of("t1", "i1"))
            .rejects(Index.of("t2", "i2"))
            .rejects(Index.of("t2", "I2"));

        final PgContext ctx = PgContext.of("CUSTOM");
        assertThat(SkipIndexesByNamePredicate.ofName(ctx, "I2"))
            .accepts(Table.of("custom.t", 0L))
            .accepts(Index.of("custom.t1", "custom.i1"))
            .rejects(Index.of("custom.t2", "custom.i2"))
            .rejects(Index.of("custom.T2", "custom.I2"));
    }

    @Test
    void shouldWorkForMultipleIndexes() {
        assertThat(SkipIndexesByNamePredicate.ofPublic(Set.of("i1", "I2")))
            .accepts(Table.of("t", 0L))
            .accepts(SequenceState.of("s11", "int", 80.0))
            .accepts(ColumnWithSerialType.ofSerial(Column.ofNullable("t", "c"), "s1"))
            .rejects(Index.of("t1", "i1"))
            .rejects(Index.of("t2", "i1"))
            .rejects(Index.of("T2", "I2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithCustomSchema(final String schemaName) {
        final PgContext ctx = PgContext.of(schemaName);
        assertThat(SkipIndexesByNamePredicate.of(ctx, Set.of("i1", "i2")))
            .accepts(Table.of(ctx.enrichWithSchema("t"), 0L))
            .accepts(Index.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i11")))
            .rejects(Index.of(ctx.enrichWithSchema("t2"), ctx.enrichWithSchema("i2")));
    }
}
