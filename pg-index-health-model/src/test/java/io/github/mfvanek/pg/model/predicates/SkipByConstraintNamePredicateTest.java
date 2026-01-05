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

import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SkipByConstraintNamePredicateTest {

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionWhenInvalidDataPassed() {
        assertThatThrownBy(() -> SkipByConstraintNamePredicate.ofName(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("constraintNameToSkip cannot be null");

        assertThatThrownBy(() -> SkipByConstraintNamePredicate.ofName(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("constraintNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByConstraintNamePredicate.ofName("   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("constraintNameToSkip cannot be blank");

        assertThatThrownBy(() -> SkipByConstraintNamePredicate.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("rawNamesToSkip cannot be null");
    }

    @Test
    void shouldNotCastObjectsWhenExclusionsIsEmpty() {
        final Constraint mockConstraint = Mockito.mock(Constraint.class);
        assertThat(SkipByConstraintNamePredicate.of(List.of()))
            .accepts(mockConstraint);
        Mockito.verify(mockConstraint, Mockito.never()).getConstraintName();
    }

    @Test
    void shouldWorkForSingleItem() {
        assertThat(SkipByConstraintNamePredicate.ofName("C1"))
            .accepts(Table.of("t"))
            .accepts(Index.of("t1", "i1"))
            .accepts(Constraint.ofType("t1", "c2", ConstraintType.CHECK))
            .accepts(ForeignKey.ofNotNullColumn("t1", "c2", "col1"))
            .accepts(DuplicatedForeignKeys.of(
                ForeignKey.ofNotNullColumn("t1", "c2", "col1"),
                ForeignKey.ofNotNullColumn("t1", "c3", "col1")
            ))
            .rejects(Constraint.ofType("t1", "c1", ConstraintType.CHECK))
            .rejects(ForeignKey.ofNotNullColumn("t1", "c1", "col1"))
            .rejects(DuplicatedForeignKeys.of(
                ForeignKey.ofNotNullColumn("t1", "c2", "col1"),
                ForeignKey.ofNotNullColumn("t1", "c1", "col1")
            ));
    }

    @Test
    void shouldWorkForMultipleItems() {
        assertThat(SkipByConstraintNamePredicate.of(Set.of("c1", "C2")))
            .accepts(Table.of("t"))
            .accepts(Constraint.ofType("t1", "c3", ConstraintType.CHECK))
            .accepts(ForeignKey.ofNotNullColumn("t1", "c3", "col1"))
            .rejects(Constraint.ofType("t1", "C1", ConstraintType.CHECK))
            .rejects(ForeignKey.ofNotNullColumn("t1", "c1", "col1"))
            .rejects(DuplicatedForeignKeys.of(
                ForeignKey.ofNotNullColumn("t1", "C2", "col1"),
                ForeignKey.ofNotNullColumn("t1", "C1", "col1")
            ));
    }
}
