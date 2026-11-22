/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.constraint;

import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ForeignKeyCycleTest {

    @Test
    void objectTypeShouldBeConstraint() {
        final ForeignKeyCycle cycle = prepare();
        assertThat(cycle.getObjectType())
            .isEqualTo(PgObjectType.CONSTRAINT);
    }

    @Test
    void testToString() {
        final ForeignKeyCycle cycle = prepare();
        assertThat(cycle)
            .hasToString("""
                ForeignKeyCycle{foreignKeys=[\
                ForeignKey{tableName='t1', constraintName='c1', columns=[Column{tableName='t1', columnName='col1', notNull=true, columnType='integer'}]}, \
                ForeignKey{tableName='t2', constraintName='c2', columns=[Column{tableName='t2', columnName='col2', notNull=false, columnType='integer'}]}]}""");
    }

    @Test
    void internalCollectionShouldBeUnmodifiable() {
        final ForeignKeyCycle cycle = prepare();
        assertThat(cycle.getTableName())
            .isEqualTo("t1");
        assertThat(cycle.getTableNames())
            .containsExactly("t1", "t2")
            .isUnmodifiable();
        assertThat(cycle.getName())
            .isEqualTo("c1,c2");
        assertThat(cycle.getForeignKeys())
            .hasSize(2)
            .isUnmodifiable();
        assertThat(cycle.getConstraints())
            .hasSize(2)
            .isUnmodifiable();
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void shouldThrowExceptionOnInvalidArguments() {
        assertThatThrownBy(() -> ForeignKeyCycle.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("foreignKeys cannot be null");
        final List<ForeignKey> empty = List.of();
        assertThatThrownBy(() -> ForeignKeyCycle.of(empty))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("foreignKeys should contain at least two items");
        final List<ForeignKey> single = List.of(ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"));
        assertThatThrownBy(() -> ForeignKeyCycle.of(single))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("foreignKeys should contain at least two items");
        final List<ForeignKey> sameTable = List.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"),
            ForeignKey.ofNotNullColumn("t2", "c2", "col1", "integer"),
            ForeignKey.ofNotNullColumn("t1", "c3", "col2", "integer"));
        assertThatThrownBy(() -> ForeignKeyCycle.of(sameTable))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name must not be repeated within a cycle");
        final ForeignKey first = ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer");
        final ForeignKey second = ForeignKey.ofNotNullColumn("t2", "c2", "col1", "integer");
        assertThatThrownBy(() -> ForeignKeyCycle.of(null, second))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("firstObject cannot be null");
        assertThatThrownBy(() -> ForeignKeyCycle.of(first, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("secondObject cannot be null");
        assertThatThrownBy(() -> ForeignKeyCycle.of(first, second, first))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name must not be repeated within a cycle");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final ForeignKeyCycle first = prepare();
        final ForeignKeyCycle theSame = prepare();
        final ForeignKeyCycle second = ForeignKeyCycle.of(List.of(
            ForeignKey.ofNotNullColumn("t1", "c3", "col2", "integer"),
            ForeignKey.ofNotNullColumn("t2", "c4", "col2", "integer")));
        final ForeignKeyCycle reversed = ForeignKeyCycle.of(
            ForeignKey.ofNullableColumn("t2", "c2", "col2", "integer"),
            ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"));

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        // order of foreign keys matters
        assertThat(reversed)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ForeignKeyCycle.class)
            .withIgnoredFields("foreignKeysNames")
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void compareToTest() {
        final ForeignKeyCycle first = prepare();
        final ForeignKeyCycle theSame = prepare();
        final ForeignKeyCycle longer = ForeignKeyCycle.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"),
            ForeignKey.ofNullableColumn("t2", "c2", "col2", "integer"),
            ForeignKey.ofNotNullColumn("t3", "c3", "col3", "integer"));
        final ForeignKeyCycle secondName = ForeignKeyCycle.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"),
            ForeignKey.ofNotNullColumn("t2", "c4", "col2", "integer"));
        final ForeignKeyCycle secondTable = ForeignKeyCycle.of(
            ForeignKey.ofNotNullColumn("t2", "c1", "col1", "integer"),
            ForeignKey.ofNotNullColumn("t3", "c2", "col1", "integer"));

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first)
            .isEqualByComparingTo(theSame)
            .isLessThan(longer)
            .isLessThan(secondName)
            .isLessThan(secondTable);

        assertThat(longer)
            .isGreaterThan(first)
            .isLessThan(secondName)
            .isLessThan(secondTable);

        assertThat(secondName)
            .isGreaterThan(first)
            .isGreaterThan(longer)
            .isLessThan(secondTable);

        assertThat(secondTable)
            .isGreaterThan(first)
            .isGreaterThan(longer)
            .isGreaterThan(secondName);
    }

    @NonNull
    private ForeignKeyCycle prepare() {
        return ForeignKeyCycle.of(
            ForeignKey.ofNotNullColumn("t1", "c1", "col1", "integer"),
            ForeignKey.ofNullableColumn("t2", "c2", "col2", "integer"));
    }
}
