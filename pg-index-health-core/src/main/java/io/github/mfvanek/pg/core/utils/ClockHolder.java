/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import java.time.Clock;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

/**
 * Utility class to work with {@link Clock}.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public final class ClockHolder {

    private static final Logger LOGGER = Logger.getLogger(ClockHolder.class.getName());
    private static final AtomicReference<Clock> CLOCK_REFERENCE = new AtomicReference<>(Clock.systemUTC());

    private ClockHolder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Allows getting currently set {@code Clock} instance.
     *
     * @return {@code Clock} instance
     */
    @SuppressWarnings("NullAway")
    public static Clock clock() {
        return CLOCK_REFERENCE.get(); // cannot be null
    }

    /**
     * Atomically sets the {@link #CLOCK_REFERENCE} to {@code newClock} and returns the old value.
     *
     * @param newClock the new value
     * @return the previous value of a clock
     */
    public static Clock setClock(final Clock newClock) {
        Objects.requireNonNull(newClock, "newClock cannot be null");
        final Clock oldClock = CLOCK_REFERENCE.getAndSet(newClock);
        LOGGER.fine(() -> String.format(Locale.ROOT, "Set new clock %s. Old clock was %s", newClock, oldClock));
        return oldClock;
    }
}
