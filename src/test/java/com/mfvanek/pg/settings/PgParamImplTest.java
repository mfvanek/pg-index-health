/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgParamImplTest {

    @Test
    void getNameAndValue() {
        final var param = PgParamImpl.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("statement_timeout", param.getName());
        assertEquals("2s", param.getValue());
    }

    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgParamImpl.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("", null));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("  ", null));
        assertThrows(NullPointerException.class, () -> PgParamImpl.of("param_name", null));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("param_name", ""));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("param_name", "  "));
    }

    @Test
    void testToString() {
        final var param = PgParamImpl.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("PgParamImpl{name='statement_timeout', value='2s'}", param.toString());
    }
}
