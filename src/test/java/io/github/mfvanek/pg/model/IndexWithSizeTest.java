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

class IndexWithSizeTest {

    @Test
    void indexWithZeroSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 0L);
        assertEquals(0L, index.getIndexSizeInBytes());
    }

    @Test
    void indexWithPositiveSize() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 123L);
        assertEquals(123L, index.getIndexSizeInBytes());
    }

    @Test
    void indexWithNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> IndexWithSize.of("t", "i", -1L));
    }

    @Test
    void testToString() {
        final IndexWithSize index = IndexWithSize.of("t", "i", 33L);
        assertEquals("IndexWithSize{tableName='t', indexName='i', indexSizeInBytes=33}", index.toString());
    }
}
