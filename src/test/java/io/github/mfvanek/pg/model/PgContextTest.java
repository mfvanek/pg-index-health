/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PgContextTest {

    @Test
    void getSchemaNameForCustomSchema() {
        final PgContext pgContext = PgContext.of("s");
        assertEquals("s", pgContext.getSchemaName());
        assertFalse(pgContext.isDefaultSchema());
    }

    @Test
    void getSchemaNameForPublicSchema() {
        final PgContext pgContext = PgContext.ofPublic();
        assertEquals("public", pgContext.getSchemaName());
        assertTrue(pgContext.isDefaultSchema());
    }

    @Test
    void getSchemaNameForPublicSchemaWithUpperCase() {
        final PgContext pgContext = PgContext.of("PUBLIC");
        assertEquals("public", pgContext.getSchemaName());
        assertTrue(pgContext.isDefaultSchema());
    }

    @Test
    void getBloatPercentageThreshold() {
        assertEquals(10, PgContext.of("s").getBloatPercentageThreshold());
        assertEquals(22, PgContext.of("s", 22).getBloatPercentageThreshold());
        assertEquals(10, PgContext.ofPublic().getBloatPercentageThreshold());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgContext.of(null));
        assertThrows(IllegalArgumentException.class, () -> PgContext.of(""));
        assertThrows(IllegalArgumentException.class, () -> PgContext.of("   "));
        assertThrows(IllegalArgumentException.class, () -> PgContext.of("s", -1));
    }

    @Test
    void testToString() {
        assertEquals("PgContext{schemaName='s', bloatPercentageThreshold=10}", PgContext.of("s").toString());
        assertEquals("PgContext{schemaName='s', bloatPercentageThreshold=11}", PgContext.of("s", 11).toString());
        assertEquals("PgContext{schemaName='public', bloatPercentageThreshold=10}", PgContext.ofPublic().toString());
    }

    @Test
    void complementWithCustomSchema() {
        final PgContext pgContext = PgContext.of("TEST");
        assertEquals("test.table1", pgContext.enrichWithSchema("table1"));
        assertEquals("test.index1", pgContext.enrichWithSchema("index1"));
        assertEquals("test.table2", pgContext.enrichWithSchema("test.table2"));
        assertEquals("TEST.table2", pgContext.enrichWithSchema("TEST.table2"));
    }

    @Test
    void complementWithPublicSchema() {
        final PgContext pgContext = PgContext.ofPublic();
        assertEquals("table1", pgContext.enrichWithSchema("table1"));
        assertEquals("index1", pgContext.enrichWithSchema("index1"));
        assertEquals("public.table2", pgContext.enrichWithSchema("public.table2"));
        assertEquals("PUBLIC.table2", pgContext.enrichWithSchema("PUBLIC.table2"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void complementWithSchemaWithInvalidArguments() {
        final PgContext pgContext = PgContext.ofPublic();
        assertThrows(NullPointerException.class, () -> pgContext.enrichWithSchema(null));
        assertThrows(IllegalArgumentException.class, () -> pgContext.enrichWithSchema(""));
        assertThrows(IllegalArgumentException.class, () -> pgContext.enrichWithSchema("   "));
    }
}
