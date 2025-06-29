/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;

import java.util.Locale;

/**
 * An adapter for the {@link LoggingKey} interface that provides simple logging keys
 * based on a {@link Diagnostic} instance.
 *
 * <p>This class implements the {@link LoggingKey} interface by deriving the key name,
 * sub-key name, and description from the given {@link Diagnostic} object.</p>
 */
public final class SimpleLoggingKeyAdapter implements LoggingKey {

    /**
     * The diagnostic instance used to derive logging key details.
     */
    private final Diagnostic diagnostic;

    private SimpleLoggingKeyAdapter(final Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
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
        return diagnostic.name().toLowerCase(Locale.ROOT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return getSubKeyName().replace('_', ' ');
    }

    /**
     * Creates a new {@link LoggingKey} instance for the specified {@link Diagnostic}.
     *
     * @param diagnostic the diagnostic instance, must not be {@code null}.
     * @return a new {@link SimpleLoggingKeyAdapter} instance.
     */
    public static LoggingKey of(final Diagnostic diagnostic) {
        return new SimpleLoggingKeyAdapter(diagnostic);
    }
}
