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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class TableWithMissingIndexTest {

    @Test
    void getters() {
        final TableWithMissingIndex table = TableWithMissingIndex.of("t", 1L, 2L, 3L);
        assertThat(table.getTableName())
                .isEqualTo("t")
                .isEqualTo(table.getName());
        assertThat(table.getTableSizeInBytes()).isEqualTo(1L);
        assertThat(table.getSeqScans()).isEqualTo(2L);
        assertThat(table.getIndexScans()).isEqualTo(3L);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void invalidArguments() {
        assertThatThrownBy(() -> TableWithMissingIndex.of(null, 0, 0, 0)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> TableWithMissingIndex.of("", 0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithMissingIndex.of(" ", 0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", -1, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", 0, -1, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TableWithMissingIndex.of("t", 0, 0, -1)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testToString() {
        assertThat(TableWithMissingIndex.of("t", 11L, 33L, 22L))
                .hasToString("TableWithMissingIndex{tableName='t', tableSizeInBytes=11, seqScans=33, indexScans=22}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);

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
        assertThat(third)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first)
                .isNotEqualTo(theSame)
                .doesNotHaveSameHashCodeAs(theSame);

        // another Table
        final TableWithBloat anotherType = TableWithBloat.of("t1", 4L, 11L, 50);
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
                .isEqualTo(first) //NOSONAR
                .hasSameHashCodeAs(first);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithMissingIndex.class)
                .withIgnoredFields("tableSizeInBytes", "seqScans", "indexScans")
                .verify();
    }

    @Test
    void compareToTest() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);
        assertThat(first)
                .isEqualByComparingTo(first)
                .isEqualByComparingTo(theSame)
                .isLessThan(third);

        assertThat(third)
                .isGreaterThan(first)
                .isGreaterThan(theSame);
    }
}
