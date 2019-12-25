/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableWithMissingIndexTest {

    @Test
    void getters() {
        final var table = TableWithMissingIndex.of("t", 1L, 2L, 3L);
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
        final var table = TableWithMissingIndex.of("t", 11L, 33L, 22L);
        assertEquals("TableWithMissingIndex{tableName='t', tableSizeInBytes=11, seqScans=33, indexScans=22}",
                table.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final var first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final var theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final var third = TableWithMissingIndex.of("t2", 3L, 4, 5);

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
        final var first = TableWithMissingIndex.of("t1", 1L, 0, 1);
        final var theSame = TableWithMissingIndex.of("t1", 2L, 2, 3);
        final var third = TableWithMissingIndex.of("t2", 3L, 4, 5);
        assertEquals(0, first.compareTo(theSame));
        assertEquals(-1, first.compareTo(third));
        assertEquals(1, third.compareTo(theSame));
    }
}
