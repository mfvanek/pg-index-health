/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TableWithBloatTest {

    @Test
    void getBloatSizeInBytes() {
        final TableWithBloat bloat = TableWithBloat.of("t", 10L, 2L, 20);
        assertThat(bloat.getBloatSizeInBytes()).isEqualTo(2L);
    }

    @Test
    void getBloatPercentage() {
        final TableWithBloat bloat = TableWithBloat.of("t", 5L, 1L, 25);
        assertThat(bloat.getBloatPercentage()).isEqualTo(25);
    }

    @Test
    void testToString() {
        assertThat(TableWithBloat.of("t", 2L, 1L, 50))
                .hasToString("TableWithBloat{tableName='t', tableSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50}");
    }

    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> TableWithBloat.of("t", 0L, -1L, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithBloat.of("t", 0L, 0L, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithBloat.of("t", -1L, 0L, 0)).isInstanceOf(IllegalArgumentException.class);
        final TableWithBloat bloat = TableWithBloat.of("t", 0L, 0L, 0);
        assertThat(bloat).isNotNull();
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
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

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
        final TableWithMissingIndex anotherType = TableWithMissingIndex.of("t1", 1L, 0, 1);
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
                .isEqualTo(first) //NOSONAR
                .hasSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithBloat.class)
                .withIgnoredFields("tableSizeInBytes", "bloatSizeInBytes", "bloatPercentage")
                .verify();
    }
}
