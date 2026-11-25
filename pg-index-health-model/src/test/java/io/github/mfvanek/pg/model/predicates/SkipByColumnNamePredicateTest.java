/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class SkipByColumnNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofNames(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofNames(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofNames("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final Constraint mockConstraint = mock(Constraint.class);
        assertThat(SkipByColumnNamePredicate.of(List.of()))
            .accepts(mockConstraint);
        verify(mockConstraint, never()).getConstraintName();
    }

    @Test
    void shouldWorkForSingleItem() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(SkipByColumnNamePredicate.ofNames("COL1"))
            .accepts(Table.of("t"))
            .accepts(Index.of("t1", "i1"))
            .accepts(Column.of("t1", "col2", true, "integer"))
            .accepts(ColumnWithSerialType.of(Column.of("t1", "col2", true, "serial"), "s1"))
            .accepts(IndexWithColumns.ofInteger(ctx, "t1", "i1", "col2"))
            .accepts(TableWithColumns.withoutColumns(Table.of("t2")))
            .rejects(Column.of("t1", "col1", true, "integer"))
            .rejects(ColumnWithSerialType.of(Column.of("t1", "col1", true, "serial"), "s1"))
            .rejects(IndexWithColumns.ofInteger(ctx, "t1", "i1", "col1"))
            .rejects(TableWithColumns.ofNullableInteger(ctx, "t3", "col1"));
    }

    @Test
    void shouldWorkForMultipleItems() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(SkipByColumnNamePredicate.of(List.of("col1", "cOL2")))
            .accepts(Column.of("t1", "col3", true, "integer"))
            .accepts(IndexWithColumns.ofInteger(ctx, "t1", "i1", "col3"))
            .accepts(TableWithColumns.withoutColumns(Table.of("t2")))
            .rejects(Column.of("t1", "col1", true, "integer"))
            .rejects(ColumnWithSerialType.of(Column.of("t1", "col2", true, "serial"), "s1"))
            .rejects(IndexWithColumns.ofInteger(ctx, "t1", "i1", "col1"))
            .rejects(TableWithColumns.ofNullableInteger(ctx, "t3", "col1"));
        assertThat(SkipByColumnNamePredicate.ofNames("col1", "cOL2", "col1"))
            .accepts(Column.of("t1", "col3", true, "integer"))
            .rejects(Column.of("t1", "col1", true, "integer"))
            .rejects(Column.of("t1", "col2", true, "integer"));
    }
}
