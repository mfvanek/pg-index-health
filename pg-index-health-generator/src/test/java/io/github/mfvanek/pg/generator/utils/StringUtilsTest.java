/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import ch.qos.logback.classic.Level;
import io.github.mfvanek.pg.support.LogsCaptor;
import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringUtilsTest {

    private static final String TARGET = "abcqwe";

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
    void truncationShouldBePerformed() {
        try (LogsCaptor logsCaptor = new LogsCaptor(StringUtils.class, Level.TRACE)) {
            assertThat(StringUtils.truncate(TARGET, 0))
                .isEmpty();
            assertThat(StringUtils.truncate(TARGET, 1))
                .isEqualTo("a");
            assertThat(StringUtils.truncate(TARGET, 2))
                .isEqualTo("ab");
            assertThat(StringUtils.truncate(TARGET, 5))
                .isEqualTo("abcqw");

            assertThat(logsCaptor.getLogs())
                .hasSize(4);
        }
    }

    @Test
    void truncationShouldNotBePerformed() {
        try (LogsCaptor logsCaptor = new LogsCaptor(StringUtils.class, Level.TRACE)) {
            assertThat(StringUtils.truncate(TARGET, 6))
                .isEqualTo("abcqwe")
                .isSameAs(TARGET);
            assertThat(StringUtils.truncate(TARGET, 7))
                .isEqualTo("abcqwe")
                .isSameAs(TARGET);

            assertThat(logsCaptor.getLogs())
                .isEmpty();
        }
    }
}
