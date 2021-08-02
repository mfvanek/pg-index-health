/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableWithBloatTest {

    @Test
    void getBloatSizeInBytes() {
        final TableWithBloat bloat = TableWithBloat.of("t", 10L, 2L, 20);
        assertEquals(2L, bloat.getBloatSizeInBytes());
    }

    @Test
    void getBloatPercentage() {
        final TableWithBloat bloat = TableWithBloat.of("t", 5L, 1L, 25);
        assertEquals(25, bloat.getBloatPercentage());
    }

    @Test
    void testToString() {
        final TableWithBloat bloat = TableWithBloat.of("t", 2L, 1L, 50);
        assertNotNull(bloat);
        assertEquals(
                "TableWithBloat{tableName='t', tableSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50}",
                bloat.toString());
    }

    @Test
    void withInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> TableWithBloat.of("t", 0L, -1L, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithBloat.of("t", 0L, 0L, -1));
        assertThrows(IllegalArgumentException.class, () -> TableWithBloat.of("t", -1L, 0L, 0));
        final TableWithBloat bloat = TableWithBloat.of("t", 0L, 0L, 0);
        assertNotNull(bloat);
    }

    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    @Test
    void equalsAndHashCode() {
        final long tableSize = 22L;
        final TableWithBloat first = TableWithBloat.of("t1", tableSize, 11L, 50);
        final TableWithBloat theSame = TableWithBloat.of("t1", tableSize, 11L, 50);
        final TableWithBloat second = TableWithBloat.of("t2", 30L, 3L, 10);
        final TableWithBloat third = TableWithBloat.of("t3", tableSize, 11L, 50);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, theSame);

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());

        // another Table
        final TableWithMissingIndex anotherType = TableWithMissingIndex.of("t1", 1L, 0, 1);
        assertEquals(first, anotherType);
        assertEquals(first.hashCode(), anotherType.hashCode());
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(TableWithBloat.class)
                .withIgnoredFields("tableSizeInBytes", "bloatSizeInBytes", "bloatPercentage")
                .verify();
    }
}
