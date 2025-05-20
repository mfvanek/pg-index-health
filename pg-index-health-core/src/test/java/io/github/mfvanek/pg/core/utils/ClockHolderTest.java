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

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.logging.Level;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ClockHolderTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ClockHolder.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void clockDefaultValue() {
        assertThat(ClockHolder.clock())
            .isNotNull()
            .isEqualTo(Clock.systemUTC());
    }

    @Test
    void setClockShouldWork() {
        try (LogsCaptor ignored = new LogsCaptor(ClockHolder.class, Level.FINEST)) {
            final LocalDateTime dateTime = LocalDateTime.of(1999, Month.DECEMBER, 31, 23, 59, 59);
            final Clock fixed = Clock.fixed(dateTime.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
            final Clock oldClock = ClockHolder.setClock(fixed);
            try {
                assertThat(ClockHolder.clock())
                    .isNotNull()
                    .isSameAs(fixed);
                assertThat(LocalDateTime.now(ClockHolder.clock()))
                    .isNotNull()
                    .isEqualTo(dateTime);
            } finally {
                ClockHolder.setClock(oldClock);
            }
        }
    }
}
