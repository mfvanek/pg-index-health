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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipTablesByNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofDefault(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> SkipTablesByNamePredicate.of(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName(ctx, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofName(ctx, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");
    }

    @Test
    void shouldWorkForSingleItem() {
        assertThat(SkipTablesByNamePredicate.ofName("t"))
            .accepts(Index.of("t1", "i1"))
            .rejects(Index.of("t", "i"))
            .rejects(Index.of("T", "I"));

        final PgContext ctx = PgContext.of("custom");
        assertThat(SkipTablesByNamePredicate.ofName(ctx, "t"))
            .accepts(Index.of("custom.t1", "custom.i1"))
            .rejects(Index.of("custom.t", "custom.i"))
            .rejects(Index.of("custom.T", "custom.I"));
    }

    @Test
    void shouldWorkForMultipleItems() {
        assertThat(SkipTablesByNamePredicate.ofDefault(Set.of("t", "T2")))
            .accepts(Index.of("t1", "i1"))
            .rejects(Index.of("t", "i"))
            .rejects(Index.of("T", "I"))
            .rejects(Index.of("t2", "i2"));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void shouldWorkWithCustomSchema(final String schemaName) {
        final PgContext ctx = PgContext.of(schemaName);
        assertThat(SkipTablesByNamePredicate.of(ctx, Set.of("t2", "T1")))
            .accepts(Table.of(ctx, "t"))
            .accepts(Index.of(ctx, "T", "I"))
            .accepts(SequenceState.of(ctx, "s", "int", 100.0))
            .rejects(Index.of(ctx, "t1", "i1"))
            .rejects(Index.of(ctx, "T2", "i2"))
            .accepts(Table.of(ctx, "t11"));
    }
}
