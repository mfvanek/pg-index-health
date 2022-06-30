/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.settings;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgParamImplTest {

    @Test
    void getNameAndValue() {
        final PgParam param = PgParamImpl.of("statement_timeout", "2s");
        assertThat(param).isNotNull();
        assertThat(param.getName()).isEqualTo("statement_timeout");
        assertThat(param.getValue()).isEqualTo("2s");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> PgParamImpl.of(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("name cannot be null");
        assertThatThrownBy(() -> PgParamImpl.of("", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name cannot be blank");
        assertThatThrownBy(() -> PgParamImpl.of("  ", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("name cannot be blank");
        assertThatThrownBy(() -> PgParamImpl.of("param_name", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("value for 'param_name' cannot be null");
    }

    @Test
    void withEmptyValue() {
        PgParam param = PgParamImpl.of("statement_timeout", "");
        assertThat(param).isNotNull();
        assertThat(param.getValue()).isEmpty();

        param = PgParamImpl.of("statement_timeout", "       ");
        assertThat(param).isNotNull();
        assertThat(param.getValue()).isEmpty();
    }

    @Test
    void testToString() {
        assertThat(PgParamImpl.of("statement_timeout", "2s"))
                .hasToString("PgParamImpl{name='statement_timeout', value='2s'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgParam first = PgParamImpl.of("statement_timeout", "2s");
        final PgParam theSame = PgParamImpl.of("statement_timeout", "2s");
        final PgParam second = PgParamImpl.of("lock_timeout", "2s");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
                .isEqualTo(first)
                .hasSameHashCodeAs(first);

        // others
        assertThat(second)
                .isNotEqualTo(first)
                .doesNotHaveSameHashCodeAs(first);

        // another implementation of PgParam
        final PgParam pgParamMock = Mockito.mock(PgParam.class);
        Mockito.when(pgParamMock.getName()).thenReturn("statement_timeout");
        assertThat(first).isEqualTo(pgParamMock);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgParamImpl.class)
                .withIgnoredFields("value")
                .verify();
    }
}
