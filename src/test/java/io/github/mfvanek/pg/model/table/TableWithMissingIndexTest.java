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

class TableWithMissingIndexTest {

    @Test
    void getters() {
        final TableWithMissingIndex table = TableWithMissingIndex.of("t", 1L, 2L, 3L);
        assertEquals("t", table.getTableName());
        assertEquals(1L, table.getTableSizeInBytes());
        assertEquals(2L, table.getSeqScans());
        assertEquals(3L, table.getIndexScans());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void invalidArguments() {
        assertThrows(NullPointerException.class, () -> TableWithMissingIndex.of(null, 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("", 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of(" ", 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("t", -1, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("t", 0, -1, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("t", 0, 0, -1));
    }

    @Test
    void testToString() {
        final TableWithMissingIndex table = TableWithMissingIndex.of("t", 11L, 33L, 22L);
        assertEquals("TableWithMissingIndex{tableName='t', tableSizeInBytes=11, seqScans=33, indexScans=22}",
                table.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // another type
        final Table anotherType = Table.of("t1", 1L);
        assertNotEquals(first, anotherType);
        assertEquals(first.hashCode(), anotherType.hashCode());

        // the same
        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        // others
        assertNotEquals(first, third);
        assertNotEquals(third, first);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(theSame, third);
        assertNotEquals(theSame.hashCode(), third.hashCode());
    }

    @Test
    void compareToTest() {
        final TableWithMissingIndex first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final TableWithMissingIndex theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final TableWithMissingIndex third = TableWithMissingIndex.of("t2", 3L, 4, 5);
        assertEquals(0, first.compareTo(theSame));
        assertEquals(-1, first.compareTo(third));
        assertEquals(1, third.compareTo(theSame));
    }
}
