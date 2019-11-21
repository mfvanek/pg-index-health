/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexWithNullsTest {

    @Test
    void getNullableField() {
        final var index = IndexWithNulls.of("t", "i", 11L, "f");
        assertEquals("t", index.getTableName());
        assertEquals("i", index.getIndexName());
        assertEquals(11L, index.getIndexSizeInBytes());
        assertEquals("f", index.getNullableField());
    }

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
        final var index = IndexWithNulls.of("t", "i", 22L, "f");
        assertEquals("IndexWithNulls{tableName='t', indexName='i', " +
                "indexSizeInBytes=22, nullableField='f'}", index.toString());
    }
}
