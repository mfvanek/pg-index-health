/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

/**
 * Represents a service that determines if a given database connection is established with a primary host.
 */
@FunctionalInterface
public interface PrimaryHostDeterminer {

    /**
     * Determines whether given connection is a connection to a primary host.
     *
     * @param pgConnection {@code PgConnection} object
     * @return {@code true} if this is a connection to a primary host.
     */
    boolean isPrimary(PgConnection pgConnection);
}
