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
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipByColumnNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByColumnNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final Constraint mockConstraint = Mockito.mock(Constraint.class);
        assertThat(SkipByColumnNamePredicate.of(List.of()))
            .accepts(mockConstraint);
        Mockito.verify(mockConstraint, Mockito.never()).getConstraintName();
    }

    @Test
    void shouldWorkForSingleItem() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(SkipByColumnNamePredicate.ofName("COL1"))
            .accepts(Table.of("t"))
            .accepts(Index.of("t1", "i1"))
            .accepts(Column.ofNotNull("t1", "col2"))
            .accepts(ColumnWithSerialType.ofSerial(Column.ofNotNull("t1", "col2"), "s1"))
            .accepts(IndexWithColumns.ofNotNull(ctx, "t1", "i1", "col2"))
            .accepts(TableWithColumns.withoutColumns(Table.of("t2")))
            .rejects(Column.ofNotNull("t1", "col1"))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNotNull("t1", "col1"), "s1"))
            .rejects(IndexWithColumns.ofNotNull(ctx, "t1", "i1", "col1"))
            .rejects(TableWithColumns.ofNullableColumn(ctx, "t3", "col1"));
    }

    @Test
    void shouldWorkForMultipleItems() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(SkipByColumnNamePredicate.of(List.of("col1", "cOL2")))
            .accepts(Column.ofNotNull("t1", "col3"))
            .accepts(IndexWithColumns.ofNotNull(ctx, "t1", "i1", "col3"))
            .accepts(TableWithColumns.withoutColumns(Table.of("t2")))
            .rejects(Column.ofNotNull("t1", "col1"))
            .rejects(ColumnWithSerialType.ofSerial(Column.ofNotNull("t1", "col2"), "s1"))
            .rejects(IndexWithColumns.ofNotNull(ctx, "t1", "i1", "col1"))
            .rejects(TableWithColumns.ofNullableColumn(ctx, "t3", "col1"));
    }
}
