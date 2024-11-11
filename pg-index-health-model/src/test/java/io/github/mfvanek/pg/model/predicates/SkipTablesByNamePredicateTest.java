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
        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");

        final PgContext ctx = PgContext.ofPublic();
        assertThatThrownBy(() -> SkipTablesByNamePredicate.of(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNamesToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable(ctx, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawTableNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable(ctx, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipTablesByNamePredicate.ofTable(ctx, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("rawTableNameToSkip cannot be blank");
    }

    @Test
    void shouldWorkForSingleTable() {
        assertThat(SkipTablesByNamePredicate.ofTable("t"))
            .accepts(Index.of("t1", "i1"))
            .rejects(Index.of("t", "i"))
            .rejects(Index.of("T", "I"));

        final PgContext ctx = PgContext.of("custom");
        assertThat(SkipTablesByNamePredicate.ofTable(ctx, "t"))
            .accepts(Index.of("custom.t1", "custom.i1"))
            .rejects(Index.of("custom.t", "custom.i"))
            .rejects(Index.of("custom.T", "custom.I"));
    }

    @Test
    void shouldWorkForMultipleTables() {
        assertThat(SkipTablesByNamePredicate.of(Set.of("t", "T2")))
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
            .accepts(Table.of(ctx.enrichWithSchema("t"), 0L))
            .accepts(Index.of(ctx.enrichWithSchema("T"), ctx.enrichWithSchema("I")))
            .accepts(SequenceState.of(ctx.enrichSequenceWithSchema("s"), "int", 100.0))
            .rejects(Index.of(ctx.enrichWithSchema("t1"), ctx.enrichWithSchema("i1")))
            .rejects(Index.of(ctx.enrichWithSchema("T2"), ctx.enrichWithSchema("i2")))
            .accepts(Table.of(ctx.enrichWithSchema("t11"), 0L));
    }
}