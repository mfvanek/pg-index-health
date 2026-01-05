/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.CheckNameAware;

import java.util.Locale;

/**
 * An adapter for the {@link LoggingKey} interface that provides simple logging keys
 * based on a {@link CheckNameAware} instance.
 *
 * <p>This class implements the {@link LoggingKey} interface by deriving the key name,
 * subkey name, and description from the given {@link CheckNameAware} object.</p>
 */
public final class SimpleLoggingKeyAdapter implements LoggingKey {

    /**
     * The check instance used to derive logging key details.
     */
    private final CheckNameAware check;

    private SimpleLoggingKeyAdapter(final CheckNameAware check) {
        this.check = check;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKeyName() {
        return "db_indexes_health";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubKeyName() {
        return check.getName().toLowerCase(Locale.ROOT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return getSubKeyName().replace('_', ' ');
    }

    /**
     * Creates a new {@link LoggingKey} instance for the specified {@link CheckNameAware}.
     *
     * @param check the check instance; must not be null.
     * @return a new {@link SimpleLoggingKeyAdapter} instance.
     */
    public static LoggingKey of(final CheckNameAware check) {
        return new SimpleLoggingKeyAdapter(check);
    }
}
