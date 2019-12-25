/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.index.health.logger.SimpleLoggingKey;

import javax.annotation.Nonnull;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public final class HealthLoggerAssertions {

    private HealthLoggerAssertions() {
        throw new UnsupportedOperationException();
    }

    public static void assertContainsKey(@Nonnull final List<String> logs,
                                         @Nonnull final SimpleLoggingKey key,
                                         @Nonnull final String expectedValue) {
        final var logStr = logs.stream()
                .filter(l -> l.contains(key.getSubKeyName()))
                .findFirst()
                .orElseThrow();
        assertThat(logStr, containsString(Validators.notBlank(expectedValue, "expectedValue")));
    }
}
