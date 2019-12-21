/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgContextTest {

    @Test
    void getSchemeName() {
        final var context = PgContext.of("s");
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
        final var context = PgContext.of("s");
        assertEquals("PgContext{schemaName='s'}", context.toString());
        assertEquals("PgContext{schemaName='public'}", PgContext.ofPublic().toString());
    }
}
