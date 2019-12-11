package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryUnitTest {

    @Test
    void convertToBytes() {
        assertEquals(2097152L, MemoryUnit.MB.convertToBytes(2));
        assertEquals(3221225472L, MemoryUnit.GB.convertToBytes(3));
    }
}
