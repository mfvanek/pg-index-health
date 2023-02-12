/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(StringUtils.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void truncateShouldThrowExceptionOnInvalidArguments() {
        assertThatThrownBy(() -> StringUtils.truncate(null, 1))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("str cannot be null");

        assertThatThrownBy(() -> StringUtils.truncate("", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("maxWith cannot be negative");
    }

    @Test
    void truncateShouldWork() {
        assertThat(StringUtils.truncate("abc", 0))
                .isEmpty();
        assertThat(StringUtils.truncate("abc", 1))
                .isEqualTo("a");
        assertThat(StringUtils.truncate("abc", 2))
                .isEqualTo("ab");
        assertThat(StringUtils.truncate("qwe", 3))
                .isEqualTo("qwe");
        assertThat(StringUtils.truncate("hello", 6))
                .isEqualTo("hello");
    }
}
