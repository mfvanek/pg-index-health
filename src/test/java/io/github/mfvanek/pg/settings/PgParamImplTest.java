/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.settings;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgParamImpl.of(null, null));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("", null));
        assertThrows(IllegalArgumentException.class, () -> PgParamImpl.of("  ", null));
        assertThrows(NullPointerException.class, () -> PgParamImpl.of("param_name", null));
    }

    @Test
    void withEmptyValue() {
        var param = PgParamImpl.of("statement_timeout", "");
        assertNotNull(param);
        assertEquals("", param.getValue());

        param = PgParamImpl.of("statement_timeout", "       ");
        assertNotNull(param);
        assertEquals("", param.getValue());
    }

    @Test
    void testToString() {
        final var param = PgParamImpl.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("PgParamImpl{name='statement_timeout', value='2s'}", param.toString());
    }

    @Test
    void equalsAndHashCode() {
        final var first = PgParamImpl.of("statement_timeout", "2s");
        final var theSame = PgParamImpl.of("statement_timeout", "2s");
        final var second = PgParamImpl.of("lock_timeout", "2s");

        assertNotEquals(first, null);
        assertNotEquals(first, BigDecimal.ZERO);

        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());
    }
}
