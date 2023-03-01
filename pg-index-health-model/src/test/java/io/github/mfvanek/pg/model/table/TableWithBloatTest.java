/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableWithBloatTest {

    @Test
    void gettersShouldWork() {
        final TableWithBloat bloat = TableWithBloat.of("t1", 10L, 2L, 25);
        assertThat(bloat.getTableName())
                .isEqualTo("t1")
                .isEqualTo(bloat.getName());
        assertThat(bloat.getTableSizeInBytes())
                .isEqualTo(10L);
        assertThat(bloat.getBloatSizeInBytes())
                .isEqualTo(2L);
        assertThat(bloat.getBloatPercentage())
                .isEqualTo(25);
    }

    @Test
    void testToString() {
        assertThat(TableWithBloat.of("t", 2L, 1L, 50))
                .hasToString("TableWithBloat{tableName='t', tableSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> TableWithBloat.of(null, 0L, 0L, 0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> TableWithBloat.of(null, 0L, 0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("table cannot be null");
        assertThatThrownBy(() -> TableWithBloat.of("t", 0L, -1L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bloatSizeInBytes cannot be less than zero");
        assertThatThrownBy(() -> TableWithBloat.of("t", 0L, 0L, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("bloatPercentage cannot be less than zero");
        assertThatThrownBy(() -> TableWithBloat.of("t", -1L, 0L, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tableSizeInBytes cannot be less than zero");

        assertThat(TableWithBloat.of("t", 0L, 0L, 0))
                .isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final long tableSize = 22L;
        final TableWithBloat first = TableWithBloat.of("t1", tableSize, 11L, 50);
        final TableWithBloat theSame = TableWithBloat.of("t1", tableSize, 11L, 50);
        final TableWithBloat second = TableWithBloat.of("t2", 30L, 3L, 10);
        final TableWithBloat third = TableWithBloat.of("t3", tableSize, 11L, 50);

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

        // others
        assertThat(second)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first)
                .isNotEqualTo(second)
                .doesNotHaveSameHashCodeAs(second);

        // another Table
        final Table anotherType = Table.of("t1", tableSize);
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithBloat.class)
                .withIgnoredFields("bloatSizeInBytes", "bloatPercentage")
                .verify();
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    @Test
    void compareToTest() {
        final long tableSize = 22L;
        final TableWithBloat first = TableWithBloat.of("t1", tableSize, 11L, 50);
        final TableWithBloat theSame = TableWithBloat.of("t1", 44L, 11L, 50); // different size!
        final TableWithBloat second = TableWithBloat.of("t2", 30L, 3L, 10);
        final TableWithBloat third = TableWithBloat.of("t3", tableSize, 11L, 50);

        assertThatThrownBy(() -> first.compareTo(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("other cannot be null");

        assertThat(first)
                .isEqualByComparingTo(first) // self
                .isEqualByComparingTo(theSame) // the same
                .isLessThan(second)
                .isLessThan(third);

        assertThat(second)
                .isGreaterThan(first)
                .isLessThan(third);

        assertThat(third)
                .isGreaterThan(first)
                .isGreaterThan(second);
    }
}
