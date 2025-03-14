/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import javax.annotation.Nonnull;

public final class StringUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringUtils.class);

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
    @Nonnull
    public static String truncate(@Nonnull final String str, final int maxWidth) {
        Objects.requireNonNull(str, "str cannot be null");
        if (maxWidth < 0) {
            throw new IllegalArgumentException("maxWith cannot be negative");
        }
        if (str.length() <= maxWidth) {
            return str;
        }
        LOGGER.trace("String {} will be truncated", str);
        return str.substring(0, maxWidth);
    }
}
