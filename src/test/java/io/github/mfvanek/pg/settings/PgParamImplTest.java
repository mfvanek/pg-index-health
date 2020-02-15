/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
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
        final PgParam param = PgParamImpl.of("statement_timeout", "2s");
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
        PgParam param = PgParamImpl.of("statement_timeout", "");
        assertNotNull(param);
        assertEquals("", param.getValue());

        param = PgParamImpl.of("statement_timeout", "       ");
        assertNotNull(param);
        assertEquals("", param.getValue());
    }

    @Test
    void testToString() {
        final PgParam param = PgParamImpl.of("statement_timeout", "2s");
        assertNotNull(param);
        assertEquals("PgParamImpl{name='statement_timeout', value='2s'}", param.toString());
    }

    @Test
    void equalsAndHashCode() {
        final PgParam first = PgParamImpl.of("statement_timeout", "2s");
        final PgParam theSame = PgParamImpl.of("statement_timeout", "2s");
        final PgParam second = PgParamImpl.of("lock_timeout", "2s");

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
