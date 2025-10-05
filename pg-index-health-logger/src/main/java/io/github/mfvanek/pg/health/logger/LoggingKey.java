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
     * @return the key name, never null.
     */
    String getKeyName();

    /**
     * Gets the name of the subkey associated with this key.
     *
     * @return the subkey name, never null.
     */
    String getSubKeyName();

    /**
     * Gets the description of this key.
     *
     * @return a description of the key, never null.
     */
    String getDescription();
}
