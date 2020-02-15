/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgContextTest {

    @Test
    void getSchemeName() {
        assertEquals("s", PgContext.of("s").getSchemaName());
        assertEquals("public", PgContext.ofPublic().getSchemaName());
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
}
