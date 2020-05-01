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
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexWithNullsTest {

    @Test
    void getNullableField() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 11L, "f");
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
        assertEquals(11L, index.getIndexSizeInBytes());
        assertEquals("f", index.getNullableField());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> IndexWithNulls.of(null, null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> IndexWithNulls.of("", null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> IndexWithNulls.of("  ", null, 0, null));
        assertThrows(NullPointerException.class, () -> IndexWithNulls.of("t", null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> IndexWithNulls.of("t", "", 0, null));
        assertThrows(NullPointerException.class, () -> IndexWithNulls.of("t", "i", 0, null));
        assertThrows(IllegalArgumentException.class, () -> IndexWithNulls.of("t", "i", 0, ""));
        assertThrows(IllegalArgumentException.class, () -> IndexWithNulls.of("t", "i", 0, "  "));
    }

    @Test
    void testToString() {
        final IndexWithNulls index = IndexWithNulls.of("t", "i", 22L, "f");
        assertEquals("IndexWithNulls{tableName='t', indexName='i', " +
                "indexSizeInBytes=22, nullableField='f'}", index.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        final IndexWithNulls first = IndexWithNulls.of("t1", "i1", 1, "f");
        final IndexWithNulls theSame = IndexWithNulls.of("t1", "i1", 3, "f"); // different size!
        final IndexWithNulls second = IndexWithNulls.of("t2", "i2", 2, "f");
        final IndexWithNulls third = IndexWithNulls.of("t3", "i3", 2, "t");

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
