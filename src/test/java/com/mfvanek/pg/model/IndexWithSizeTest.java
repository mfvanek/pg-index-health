/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class IndexWithSizeTest {

    @Test
    void indexWithZeroSize() {
        final var index = IndexWithSize.of("t", "i", 0L);
        assertEquals(0L, index.getIndexSizeInBytes());
    }

    @Test
    void indexWithPositiveSize() {
        final var index = IndexWithSize.of("t", "i", 123L);
        assertEquals(123L, index.getIndexSizeInBytes());
    }

    @Test
    void indexWithNegativeSize() {
        assertThrows(IllegalArgumentException.class, () -> IndexWithSize.of("t", "i", -1L));
    }

    @Test
    void testToString() {
        final var index = IndexWithSize.of("t", "i", 33L);
        assertEquals("IndexWithSize{tableName='t', indexName='i', indexSizeInBytes=33}", index.toString());
    }
}
