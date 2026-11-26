/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

/**
 * Allows getting information about capabilities of concrete PostgreSQL version.
 *
 * @author Ivan Vakhrushev
 */
public interface PostgresVersionAware {

    /**
     * Retrieves the mount volume for the PostgreSQL container.
     *
     * @return the mount volume path as a string
     * @since 0.30.0
     */
    String getMountVolume();

    /**
     * Checks whether <a href="https://www.postgresql.org/docs/18/release-18.html#RELEASE-18-CONSTRAINTS">NOT NULL</a> constraints are supported for the given PostgreSQL container.
     *
     * @return true for version 18 and higher
     * @since 0.30.0
     */
    boolean isNotNullConstraintsSupported();
}
