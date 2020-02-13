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

class UnusedIndexTest {

    @Test
    void getIndexScans() {
        final UnusedIndex index = UnusedIndex.of("t", "i", 1L, 2L);
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
        assertEquals(1L, index.getIndexSizeInBytes());
        assertEquals(2L, index.getIndexScans());
    }

    @Test
    void testToString() {
        final UnusedIndex index = UnusedIndex.of("t", "i", 1L, 2L);
        assertEquals("UnusedIndex{tableName='t', indexName='i', " +
                "indexSizeInBytes=1, indexScans=2}", index.toString());
    }

    @Test
    void indexWithNegativeScans() {
        assertThrows(IllegalArgumentException.class, () -> UnusedIndex.of("t", "i", -1L, 0L));
        assertThrows(IllegalArgumentException.class, () -> UnusedIndex.of("t", "i", 1L, -1L));
    }

    @Test
    void testEqualsAndHashCode() {
        final UnusedIndex first = UnusedIndex.of("t1", "i1", 1L, 2L);
        final UnusedIndex second = UnusedIndex.of("t1", "i2", 1L, 3L);
        final UnusedIndex third = UnusedIndex.of("t2", "i2", 2L, 2L);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, UnusedIndex.of("t1", "i1", 1L, 2L));

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }
}
