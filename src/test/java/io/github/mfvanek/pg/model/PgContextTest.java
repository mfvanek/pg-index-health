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

class PgContextTest {

    @Test
    void getSchemeName() {
        final PgContext context = PgContext.of("s");
        assertEquals("s", context.getSchemaName());
        assertEquals("public", PgContext.ofPublic().getSchemaName());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgContext.of(null));
        assertThrows(IllegalArgumentException.class, () -> PgContext.of(""));
        assertThrows(IllegalArgumentException.class, () -> PgContext.of("   "));
    }

    @Test
    void testToString() {
        final PgContext context = PgContext.of("s");
        assertEquals("PgContext{schemaName='s'}", context.toString());
        assertEquals("PgContext{schemaName='public'}", PgContext.ofPublic().toString());
    }
}
