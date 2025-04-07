/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

/**
 * Utility class to work with {@link Clock}.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public final class ClockHolder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClockHolder.class);
    private static final AtomicReference<Clock> CLOCK_REFERENCE = new AtomicReference<>(Clock.systemUTC());

    private ClockHolder() {
        throw new UnsupportedOperationException();
    }

    /**
     * Allows to get currently set {@code Clock} instance.
     *
     * @return {@code Clock} instance
     */
    @Nonnull
    public static Clock clock() {
        return CLOCK_REFERENCE.get();
    }

    /**
     * Atomically sets the {@link #CLOCK_REFERENCE} to {@code newClock} and returns the old value.
     *
     * @param newClock the new value
     * @return the previous value of clock
     */
    @Nonnull
    public static Clock setClock(@Nonnull final Clock newClock) {
        Objects.requireNonNull(newClock, "newClock cannot be null");
        final Clock oldClock = CLOCK_REFERENCE.getAndSet(newClock);
        LOGGER.debug("Set new clock {}. Old clock was {}", newClock, oldClock);
        return oldClock;
    }
}
