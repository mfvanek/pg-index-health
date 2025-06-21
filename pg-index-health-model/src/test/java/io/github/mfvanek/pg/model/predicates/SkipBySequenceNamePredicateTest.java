/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.predicates;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
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

class SkipBySequenceNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawSequenceNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawSequenceNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawSequenceNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofDefault(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> SkipBySequenceNamePredicate.of(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawSequenceNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName(ctx, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawSequenceNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.ofName(ctx, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawSequenceNameToSkip cannot be blank");
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final SequenceState mockSequence = Mockito.mock(SequenceState.class);
        assertThat(SkipBySequenceNamePredicate.ofDefault(List.of()))
            .accepts(mockSequence);
        Mockito.verify(mockSequence, Mockito.never()).getSequenceName();
    }

    @Test
    void shouldWorkForSingleItem() {
        assertThat(SkipBySequenceNamePredicate.ofName("s2"))
            .accepts(Table.of("t"))
            .accepts(SequenceState.of("s1", "int", 80.0))
            .rejects(SequenceState.of("s2", "int", 80.0))
            .rejects(SequenceState.of("S2", "int", 80.0));

        final PgContext ctx = PgContext.of("CUSTOM");
        assertThat(SkipBySequenceNamePredicate.ofName(ctx, "S2"))
            .accepts(Table.of("custom.t"))
            .accepts(SequenceState.of("custom.s1", "int", 80.0))
            .rejects(SequenceState.of("custom.s2", "int", 80.0))
            .rejects(SequenceState.of("custom.S2", "int", 80.0));
    }

    @Test
    void shouldWorkForMultipleItems() {
        assertThat(SkipBySequenceNamePredicate.ofDefault(Set.of("s1", "S2")))
            .accepts(Table.of("t"))
            .accepts(SequenceState.of("s11", "int", 80.0))
            .rejects(SequenceState.of("s1", "int", 80.0))
            .rejects(SequenceState.of("s2", "int", 80.0))
            .rejects(SequenceState.of("S2", "int", 80.0))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNullable("t", "c"), "s1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithCustomSchema(final String schemaName) {
        final PgContext ctx = PgContext.of(schemaName);
        assertThat(SkipBySequenceNamePredicate.of(ctx, Set.of("s1", "s2")))
            .accepts(Table.of(ctx, "t"))
            .accepts(SequenceState.of(ctx, "s11", "int", 80.0))
            .rejects(SequenceState.of(ctx, "s1", "int", 80.0))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNullable(ctx.enrichWithSchema("t"), "c"), ctx.enrichWithSchema("s1")));
    }
}
