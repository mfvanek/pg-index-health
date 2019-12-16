/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableWithoutPrimaryKeyTest {

    @Test
    void getTableName() {
        final var table = TableWithoutPrimaryKey.of("t");
        assertEquals("t", table.getTableName());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThrows(NullPointerException.class, () -> TableWithoutPrimaryKey.of(null));
        assertThrows(IllegalArgumentException.class, () -> TableWithoutPrimaryKey.of(""));
        assertThrows(IllegalArgumentException.class, () -> TableWithoutPrimaryKey.of("  "));
    }

    @Test
    void testToString() {
        final var table = TableWithoutPrimaryKey.of("t");
        assertEquals("TableWithoutPrimaryKey{tableName='t'}", table.toString());
    }
}
