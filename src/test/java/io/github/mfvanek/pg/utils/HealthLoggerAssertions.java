/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.common.health.logger.SimpleLoggingKey;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public final class HealthLoggerAssertions {

    private HealthLoggerAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertContainsKey(@Nonnull final List<String> logs,
                                         @Nonnull final SimpleLoggingKey key,
                                         @Nonnull final String expectedValue) {
        final String logStr = logs.stream()
                .filter(l -> l.contains(key.getSubKeyName()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No value present"));
        assertThat(logStr, containsString(Validators.notBlank(expectedValue, "expectedValue")));
    }
}
