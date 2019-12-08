package com.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgParamTest {

    @Test
    void getNameAndValue() {
        final var param = PgParam.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("statement_timeout", param.getName());
        assertEquals("2s", param.getValue());
    }

    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgParam.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> PgParam.of("", null));
        assertThrows(IllegalArgumentException.class, () -> PgParam.of("  ", null));
        assertThrows(NullPointerException.class, () -> PgParam.of("param_name", null));
        assertThrows(IllegalArgumentException.class, () -> PgParam.of("param_name", ""));
        assertThrows(IllegalArgumentException.class, () -> PgParam.of("param_name", "  "));
    }

    @Test
    void testToString() {
        final var param = PgParam.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("PgParam{name='statement_timeout', value='2s'}", param.toString());
    }
}
