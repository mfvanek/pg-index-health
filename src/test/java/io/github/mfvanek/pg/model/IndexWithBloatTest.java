/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexWithBloatTest {

    @Test
    void getBloatSizeInBytes() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 10L, 2L, 20);
        assertEquals(2L, bloat.getBloatSizeInBytes());
    }

    @Test
    void getBloatPercentage() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 5L, 1L, 25);
        assertEquals(25, bloat.getBloatPercentage());
    }

    @Test
    void testToString() {
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 2L, 1L, 50);
        assertNotNull(bloat);
        assertEquals(
                "IndexWithBloat{tableName='t', indexName='i', indexSizeInBytes=2, bloatSizeInBytes=1, bloatPercentage=50}",
                bloat.toString());
    }

    @Test
    void withInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", 0L, -1L, 0));
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", 0L, 0L, -1));
        assertThrows(IllegalArgumentException.class, () -> IndexWithBloat.of("t", "i", -1L, 0L, 0));
        final IndexWithBloat bloat = IndexWithBloat.of("t", "i", 0L, 0L, 0);
        assertNotNull(bloat);
    }

    @Test
    void equalsAndHashCode() {
        final IndexWithBloat first = IndexWithBloat.of("t1", "i1", 22L, 11L, 50);
        final IndexWithBloat second = IndexWithBloat.of("t1", "i1", 30L, 3L, 10);
        final IndexWithBloat third = IndexWithBloat.of("t1", "i2", 22L, 11L, 50);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, IndexWithBloat.of("t1", "i1", 22L, 11L, 50));

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }
}
