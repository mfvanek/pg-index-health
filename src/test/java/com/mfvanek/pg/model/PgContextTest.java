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
        assertEquals("s", context.getSchemeName());
        assertEquals("public", PgContext.ofPublic().getSchemeName());
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
        assertEquals("PgContext{schemeName='s'}", context.toString());
        assertEquals("PgContext{schemeName='public'}", PgContext.ofPublic().toString());
    }
}
