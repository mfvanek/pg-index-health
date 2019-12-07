/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableWithMissingIndexTest {

    @Test
    void getters() {
        final var table = TableWithMissingIndex.of("t", 1, 2);
        assertEquals("t", table.getTableName());
        assertEquals(1, table.getSeqScans());
        assertEquals(2, table.getIndexScans());
    }

    @Test
    void invalidArguments() {
        assertThrows(NullPointerException.class, () -> TableWithMissingIndex.of(null, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of(" ", 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("t", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> TableWithMissingIndex.of("t", 0, -1));
    }

    @Test
    void testToString() {
        final var table = TableWithMissingIndex.of("t", 33, 22);
        assertEquals("TableWithMissingIndex{tableName='t', seqScans=33, indexScans=22}", table.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final var first = TableWithMissingIndex.of("t1", 0, 1);
        final var theSame = TableWithMissingIndex.of("t1", 2, 3);
        final var third = TableWithMissingIndex.of("t2", 4, 5);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(theSame, third);
        assertNotEquals(theSame.hashCode(), third.hashCode());
    }

    @Test
    void comparison() {
        final var first = TableWithMissingIndex.of("t1", 0, 1);
        final var theSame = TableWithMissingIndex.of("t1", 2, 3);
        final var third = TableWithMissingIndex.of("t2", 4, 5);
        assertEquals(0, first.compareTo(theSame));
        assertEquals(-1, first.compareTo(third));
        assertEquals(1, third.compareTo(theSame));
    }
}
