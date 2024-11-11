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

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawSequenceNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipBySequenceNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofPublic();
        assertThatThrownBy(() -> SkipBySequenceNamePredicate.of(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawSequenceNamesToSkip cannot be null");

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
        assertThat(SkipBySequenceNamePredicate.of(List.of()))
            .accepts(mockSequence);
        Mockito.verify(mockSequence, Mockito.never()).getSequenceName();
    }

    @Test
    void shouldWorkForSingleSequence() {
        assertThat(SkipBySequenceNamePredicate.ofName("PUBLIC.s2"))
            .accepts(Table.of("t", 0L))
            .accepts(SequenceState.of("public.s1", "int", 80.0))
            .rejects(SequenceState.of("public.s2", "int", 80.0))
            .rejects(SequenceState.of("public.S2", "int", 80.0));

        final PgContext ctx = PgContext.of("CUSTOM");
        assertThat(SkipBySequenceNamePredicate.ofName(ctx, "custom.S2"))
            .accepts(Table.of("custom.t", 0L))
            .accepts(SequenceState.of("custom.s1", "int", 80.0))
            .rejects(SequenceState.of("custom.s2", "int", 80.0))
            .rejects(SequenceState.of("custom.S2", "int", 80.0));
    }

    @Test
    void shouldWorkForMultipleSequences() {
        assertThat(SkipBySequenceNamePredicate.of(Set.of("PUBLIC.s1", "public.S2")))
            .accepts(Table.of("t", 0L))
            .accepts(SequenceState.of("public.s11", "int", 80.0))
            .rejects(SequenceState.of("public.s1", "int", 80.0))
            .rejects(SequenceState.of("public.s2", "int", 80.0))
            .rejects(SequenceState.of("PUBLIC.S2", "int", 80.0))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNullable("t", "c"), "public.s1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithCustomSchema(final String schemaName) {
        final PgContext ctx = PgContext.of(schemaName);
        assertThat(SkipBySequenceNamePredicate.of(ctx, Set.of(ctx.enrichSequenceWithSchema("s1"))))
            .accepts(Table.of(ctx.enrichWithSchema("t"), 0L))
            .accepts(SequenceState.of(ctx.enrichSequenceWithSchema("s11"), "int", 80.0))
            .rejects(SequenceState.of(ctx.enrichSequenceWithSchema("s1"), "int", 80.0))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNullable(ctx.enrichWithSchema("t"), "c"), ctx.enrichSequenceWithSchema("s1")));
    }
}
