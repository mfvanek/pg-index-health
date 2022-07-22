/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;

import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

abstract class HealthLoggerTestBase extends DatabaseAwareTestBase {

    protected HealthLoggerTestBase(@Nonnull final DataSource dataSource) {
        super(dataSource);
    }

    @Nonnull
    protected static Predicate<String> ofKey(@Nonnull final SimpleLoggingKey key) {
        return new SimpleLoggingKeyPredicate(key);
    }

    private static class SimpleLoggingKeyPredicate implements Predicate<String> {

        private final SimpleLoggingKey key;

        SimpleLoggingKeyPredicate(@Nonnull final SimpleLoggingKey key) {
            this.key = Objects.requireNonNull(key);
        }

        @Override
        public boolean test(final String str) {
            return str.contains(key.getSubKeyName());
        }
    }
}
