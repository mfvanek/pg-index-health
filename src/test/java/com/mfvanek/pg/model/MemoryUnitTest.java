/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryUnitTest {

    @Test
    void convertToBytes() {
        assertEquals(2097152L, MemoryUnit.MB.convertToBytes(2));
        assertEquals(3221225472L, MemoryUnit.GB.convertToBytes(3));
    }

    @Test
    void toStringTest() {
        assertEquals("MemoryUnit{dimension=1048576, description='megabyte'}", MemoryUnit.MB.toString());
    }
}
