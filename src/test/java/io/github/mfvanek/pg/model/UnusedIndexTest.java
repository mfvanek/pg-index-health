/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnusedIndexTest {

    @Test
    void getIndexScans() {
        final var index = UnusedIndex.of("t", "i", 1L, 2L);
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
        assertEquals(1L, index.getIndexSizeInBytes());
        assertEquals(2L, index.getIndexScans());
    }

    @Test
    void testToString() {
        final var index = UnusedIndex.of("t", "i", 1L, 2L);
        assertEquals("UnusedIndex{tableName='t', indexName='i', " +
                "indexSizeInBytes=1, indexScans=2}", index.toString());
    }

    @Test
    void indexWithNegativeScans() {
        assertThrows(IllegalArgumentException.class, () -> UnusedIndex.of("t", "i", -1L, 0L));
        assertThrows(IllegalArgumentException.class, () -> UnusedIndex.of("t", "i", 1L, -1L));
    }
}
