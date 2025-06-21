/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

public final class StringUtils {

    private static final Logger LOGGER = Logger.getLogger(StringUtils.class.getName());

    private StringUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Truncates a String.
     * Similar to the {@code truncate} method from Apache commons-lang3.
     *
     * @param str      the String to truncate, cannot be null
     * @param maxWidth maximum length of result String, must be positive
     * @return truncated String
     */
    public static String truncate(final String str, final int maxWidth) {
        Objects.requireNonNull(str, "str cannot be null");
        if (maxWidth < 0) {
            throw new IllegalArgumentException("maxWith cannot be negative");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        LOGGER.finest(() -> String.format(Locale.ROOT, "String %s will be truncated", str));
        return str.substring(0, maxWidth);
    }
}
