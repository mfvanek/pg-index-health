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

    @Test
    void testEqualsAndHashCode() {
        final IndexWithSize first = IndexWithSize.of("t1", "i1", 22L);
        final IndexWithSize second = IndexWithSize.of("t1", "i1", 33L);
        final IndexWithSize third = IndexWithSize.of("t1", "i2", 22L);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, IndexWithSize.of("t1", "i1", 22L));

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }
}
