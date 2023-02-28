/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.function;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StoredFunctionTest {

    @Test
    void gettersShouldWork() {
        final StoredFunction noArgsFunction = StoredFunction.ofNoArgs("f1");
        assertThat(noArgsFunction)
                .isNotNull();
        assertThat(noArgsFunction.getFunctionName())
                .isEqualTo("f1")
                .isEqualTo(noArgsFunction.getName());
        assertThat(noArgsFunction.getFunctionSignature())
                .isEmpty();

        final StoredFunction function = StoredFunction.of("f2", "IN a integer, IN b integer, IN c integer");
        assertThat(function)
                .isNotNull();
        assertThat(function.getFunctionName())
                .isEqualTo("f2")
                .isEqualTo(function.getName());
        assertThat(function.getFunctionSignature())
                .isEqualTo("IN a integer, IN b integer, IN c integer");
    }

    @Test
    void trimShouldBeApplied() {
        final StoredFunction function = StoredFunction.of("f1", "   ");
        assertThat(function)
                .isNotNull();
        assertThat(function.getFunctionSignature())
                .isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        assertThatThrownBy(() -> StoredFunction.ofNoArgs(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("functionName cannot be null");
        assertThatThrownBy(() -> StoredFunction.ofNoArgs(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("functionName cannot be blank");
        assertThatThrownBy(() -> StoredFunction.ofNoArgs("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("functionName cannot be blank");

        assertThatThrownBy(() -> StoredFunction.of(null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("functionName cannot be null");
        assertThatThrownBy(() -> StoredFunction.of("", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("functionName cannot be blank");
        assertThatThrownBy(() -> StoredFunction.of("  ", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("functionName cannot be blank");
        assertThatThrownBy(() -> StoredFunction.of("f1", null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("functionSignature cannot be null");
    }

    @Test
    void testToString() {
        assertThat(StoredFunction.ofNoArgs("f1"))
                .hasToString("StoredFunction{functionName='f1', functionSignature=''}");

        assertThat(StoredFunction.of("f2", "IN a integer, IN b integer, IN c integer"))
                .hasToString("StoredFunction{functionName='f2', functionSignature='IN a integer, IN b integer, IN c integer'}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final StoredFunction first = StoredFunction.of("f1", "a integer");
        final StoredFunction theSame = StoredFunction.of("f1", "a integer");
        final StoredFunction second = StoredFunction.ofNoArgs("f1");
        final StoredFunction third = StoredFunction.ofNoArgs("f3");

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

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
                .doesNotHaveSameHashCodeAs(first)
                .isNotEqualTo(third)
                .doesNotHaveSameHashCodeAs(third);
    }

    @Test
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(StoredFunction.class)
                .verify();
    }

    @Test
    void compareToTest() {
        final StoredFunction first = StoredFunction.of("f1", "a integer");
        final StoredFunction theSame = StoredFunction.of("f1", "a integer");
        final StoredFunction second = StoredFunction.ofNoArgs("f1");
        final StoredFunction third = StoredFunction.ofNoArgs("f3");

        //noinspection ResultOfMethodCallIgnored,ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("other cannot be null");

        assertThat(first)
                .isEqualByComparingTo(first) // self
                .isEqualByComparingTo(theSame) // the same
                .isGreaterThan(second)
                .isLessThan(third);

        assertThat(second)
                .isLessThan(first)
                .isLessThan(third);

        assertThat(third)
                .isGreaterThan(first)
                .isGreaterThan(second);
    }
}
