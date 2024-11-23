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

import javax.annotation.Nonnull;

/**
 * Represents a logging key with associated metadata.
 *
 * @author Ivan Vakhrushev
 */
@SuppressWarnings("WeakerAccess")
public interface LoggingKey {

    /**
     * Gets the name of the key.
     *
     * @return the key name, never {@code null}.
     */
    @Nonnull
    String getKeyName();

    /**
     * Gets the name of the sub-key associated with this key.
     *
     * @return the sub-key name, never {@code null}.
     */
    @Nonnull
    String getSubKeyName();

    /**
     * Gets the description of this key.
     *
     * @return a description of the key, never {@code null}.
     */
    @Nonnull
    String getDescription();
}
