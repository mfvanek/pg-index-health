/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexTest {

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
        final var index = Index.of("t", "i");
        assertNotNull(index);
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
    }

    @Test
    void testToString() {
        final var index = Index.of("t", "i");
        assertNotNull(index);
        assertEquals("Index{tableName='t', indexName='i'}", index.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final var first = Index.of("t1", "i1");
        final var second = Index.of("t1", "i2");
        final var third = Index.of("t2", "i2");

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
}
