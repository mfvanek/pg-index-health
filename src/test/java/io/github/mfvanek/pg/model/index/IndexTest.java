/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void validation() {
        assertThrows(NullPointerException.class, () -> Index.of(null, null));
        assertThrows(NullPointerException.class, () -> Index.of("t", null));
        assertThrows(IllegalArgumentException.class, () -> Index.of("", ""));
        assertThrows(IllegalArgumentException.class, () -> Index.of(" ", " "));
        assertThrows(IllegalArgumentException.class, () -> Index.of("t", ""));
        assertThrows(IllegalArgumentException.class, () -> Index.of("t", " "));
    }

    @Test
    void getTableAndIndexName() {
        final Index index = Index.of("t", "i");
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
    }

    @Test
    void testToString() {
        final Index index = Index.of("t", "i");
        assertNotNull(index);
        assertEquals("Index{tableName='t', indexName='i'}", index.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final Index first = Index.of("t1", "i1");
        final Index second = Index.of("t1", "i2");
        final Index third = Index.of("t2", "i2");

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, Index.of("t1", "i1"));

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        assertNotEquals(first, third);
        assertNotEquals(first.hashCode(), third.hashCode());

        assertNotEquals(second, third);
        assertNotEquals(second.hashCode(), third.hashCode());
    }

    @SuppressWarnings({"ConstantConditions", "EqualsWithItself", "ResultOfMethodCallIgnored"})
    @Test
    void compareToTest() {
        final Index first = Index.of("t1", "i1");
        final Index second = Index.of("t1", "i2");
        final Index third = Index.of("t2", "i2");

        assertThrows(NullPointerException.class, () -> first.compareTo(null));

        // self
        assertEquals(0, first.compareTo(first));

        // the same
        assertEquals(0, first.compareTo(Index.of("t1", "i1")));

        // others
        assertEquals(-1, first.compareTo(second));
        assertEquals(1, second.compareTo(first));

        assertEquals(-1, second.compareTo(third));
        assertEquals(1, third.compareTo(second));
    }
}
