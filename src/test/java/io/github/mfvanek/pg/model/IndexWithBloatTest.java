/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
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
        final IndexWithBloat theSame = IndexWithBloat.of("t1", "i1", 100L, 60L, 60); // different size!
        final IndexWithBloat second = IndexWithBloat.of("t2", "i2", 30L, 3L, 10);
        final IndexWithBloat third = IndexWithBloat.of("t3", "i3", 22L, 11L, 50);

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        final Index anotherType = Index.of("t1", "i1");
        assertNotEquals(first, anotherType);
        assertEquals(first.hashCode(), anotherType.hashCode());

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
}
