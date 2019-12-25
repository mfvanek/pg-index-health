/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TableTest {

    @Test
    void getTableName() {
        final var table = Table.of("t", 1L);
        assertEquals("t", table.getTableName());
        assertEquals(1L, table.getTableSizeInBytes());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValues() {
        assertThrows(NullPointerException.class, () -> Table.of(null, 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("", 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("  ", 1L));
        assertThrows(IllegalArgumentException.class, () -> Table.of("t", -1L));
    }

    @Test
    void testToString() {
        final var table = Table.of("t", 2L);
        assertEquals("Table{tableName='t', tableSizeInBytes=2}", table.toString());
    }
}
