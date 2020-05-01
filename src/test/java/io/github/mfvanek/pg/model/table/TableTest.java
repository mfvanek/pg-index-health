/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableTest {

    @Test
    void getTableName() {
        final Table table = Table.of("t", 1L);
        assertEquals("t", table.getTableName());
        assertEquals(1L, table.getTableSizeInBytes());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThrows(NullPointerException.class, () -> Table.of(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("", 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("  ", 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("t", -1L));
    }

    @Test
    void testToString() {
        final Table table = Table.of("t", 2L);
        assertEquals("Table{tableName='t', tableSizeInBytes=2}", table.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final Table first = Table.of("t1", 22L);
        final Table theSame = Table.of("t1", 0L); // different size!
        final Table second = Table.of("t2", 30L);
        final Table third = Table.of("t3", 22L);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        // others
        assertNotEquals(first, second);
        assertNotEquals(second, first);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }

    @SuppressWarnings({"ConstantConditions", "EqualsWithItself", "ResultOfMethodCallIgnored"})
    @Test
    void compareToTest() {
        final Table first = Table.of("t1", 22L);
        final Table theSame = Table.of("t1", 0L); // different size!
        final Table second = Table.of("t2", 30L);
        final Table third = Table.of("t3", 22L);

        assertThrows(NullPointerException.class, () -> first.compareTo(null));

        // self
        assertEquals(0, first.compareTo(first));

        // the same
        assertEquals(0, first.compareTo(theSame));

        // others
        assertEquals(-1, first.compareTo(second));
        assertEquals(1, second.compareTo(first));

        assertEquals(-1, second.compareTo(third));
        assertEquals(1, third.compareTo(second));
    }
}
