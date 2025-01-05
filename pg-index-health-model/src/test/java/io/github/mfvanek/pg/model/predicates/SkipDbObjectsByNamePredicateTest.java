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

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipDbObjectsByNamePredicateTest {

    private static final PgContext CTX = PgContext.of("CUSTOM");
    private static final Table FIRST = Table.of(CTX, "TABLE1", 1L);
    private static final Table SECOND = Table.of(CTX, "TABLE2", 2L);
    private static final Table THIRD = Table.of(CTX, "TABLE3", 3L);

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipDbObjectsByNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fullyQualifiedObjectNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipDbObjectsByNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("fullyQualifiedObjectNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipDbObjectsByNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("fullyQualifiedObjectNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipDbObjectsByNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("fullyQualifiedObjectNamesToSkip cannot be null");
        assertThatThrownBy(() -> SkipDbObjectsByNamePredicate.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgContext cannot be null");
    }

    @Test
    void caseShouldNotMatter() {
        assertThat(SkipDbObjectsByNamePredicate.ofName("CUSTOM.table3"))
            .accepts(FIRST)
            .accepts(SECOND)
            .rejects(THIRD);

        assertThat(SkipDbObjectsByNamePredicate.ofName(CTX, "table3"))
            .accepts(FIRST)
            .accepts(SECOND)
            .rejects(THIRD);
    }

    @Test
    void forList() {
        assertThat(SkipDbObjectsByNamePredicate.of(List.of("CUSTOM.table1", "CUSTOM.table3")))
            .rejects(FIRST)
            .accepts(SECOND)
            .rejects(THIRD);

        assertThat(SkipDbObjectsByNamePredicate.of(CTX, List.of("table1", "table3")))
            .rejects(FIRST)
            .accepts(SECOND)
            .rejects(THIRD);
    }

    @Test
    void forEmpty() {
        final Table mockTable = Mockito.mock(Table.class);
        assertThat(SkipDbObjectsByNamePredicate.of(List.of()))
            .accepts(FIRST)
            .accepts(SECOND)
            .accepts(THIRD)
            .accepts(mockTable);
        Mockito.verify(mockTable, Mockito.never()).getName();
    }
}
