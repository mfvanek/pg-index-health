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

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryUnitTest {

    @Test
    void convertToBytes() {
        assertEquals(1024L, MemoryUnit.KB.convertToBytes(1));
        assertEquals(2097152L, MemoryUnit.MB.convertToBytes(2));
        assertEquals(3221225472L, MemoryUnit.GB.convertToBytes(3));
    }

    @Test
    void toStringTest() {
        assertEquals("MemoryUnit{dimension=1048576, description='megabyte'}", MemoryUnit.MB.toString());
    }
}
