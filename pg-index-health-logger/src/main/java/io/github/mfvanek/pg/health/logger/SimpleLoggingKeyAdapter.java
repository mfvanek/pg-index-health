/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;

import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;

/**
 * An adapter for the {@link LoggingKey} interface that provides simple logging keys
 * based on a {@link Diagnostic} instance.
 *
 * <p>This class implements the {@link LoggingKey} interface by deriving the key name,
 * sub-key name, and description from the given {@link Diagnostic} object.</p>
 */
@Immutable
@ThreadSafe
public final class SimpleLoggingKeyAdapter implements LoggingKey {

    /**
     * The diagnostic instance used to derive logging key details.
     */
    private final Diagnostic diagnostic;

    private SimpleLoggingKeyAdapter(@Nonnull final Diagnostic diagnostic) {
        this.diagnostic = diagnostic;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getKeyName() {
        return "db_indexes_health";
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String getSubKeyName() {
        return diagnostic.name().toLowerCase(Locale.ROOT);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
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
    @Nonnull
    public static LoggingKey of(@Nonnull final Diagnostic diagnostic) {
        return new SimpleLoggingKeyAdapter(diagnostic);
    }
}
