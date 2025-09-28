/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.PgConnection;

import javax.sql.DataSource;

/**
 * Factory interface for creating PostgreSQL connections and data sources.
 * Provides methods to obtain a {@link PgConnection} object or a {@link DataSource}
 * for a given PostgreSQL connection URL, username, and password.
 */
public interface PgConnectionFactory {

    /**
     * Creates a {@code PgConnection} object using the given PostgreSQL connection URL, username, and password.
     * The method establishes a connection to the specified PostgreSQL database and provides
     * an abstraction for interacting with it.
     *
     * @param pgUrl the connection URL to the PostgreSQL database; must not be null or blank
     * @param userName the username for database authentication; must not be null or blank
     * @param password the password for database authentication; must not be null or blank
     * @return a {@code PgConnection} object representing the connection to the PostgreSQL database
     */
    PgConnection forUrl(String pgUrl, String userName, String password);

    /**
     * Provides a {@link DataSource} object configured with the specified PostgreSQL connection URL,
     * username, and password. The returned {@code DataSource} can be used to manage and obtain
     * database connections efficiently.
     *
     * @param pgUrl the connection URL to the PostgreSQL database; must not be null or blank
     * @param userName the username for database authentication; must not be null or blank
     * @param password the password for database authentication; must not be null or blank
     * @return a {@link DataSource} instance configured for the provided PostgreSQL connection details
     */
    DataSource dataSourceFor(String pgUrl, String userName, String password);
}
