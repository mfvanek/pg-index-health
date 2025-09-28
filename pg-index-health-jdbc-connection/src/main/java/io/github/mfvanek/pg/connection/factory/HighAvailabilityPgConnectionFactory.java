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

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;

import java.util.Collection;

/**
 * Factory interface for creating instances of {@code HighAvailabilityPgConnection}.
 * This interface provides mechanisms for creating connections to high-availability PostgreSQL clusters,
 * ensuring access to both primary and replica hosts within the cluster.
 */
public interface HighAvailabilityPgConnectionFactory {

    /**
     * Creates instance of {@code HighAvailabilityPgConnection} with given credentials.
     *
     * @param credentials given credentials.
     * @return instance of {@code HighAvailabilityPgConnection}
     */
    HighAvailabilityPgConnection of(ConnectionCredentials credentials);

    /**
     * Creates an instance of {@code HighAvailabilityPgConnection} for a given write URL and credentials.
     *
     * @param writeUrl the write connection URL; must not be null, blank, or have an invalid format.
     * @param userName the username for authentication; must not be null or blank.
     * @param password the password for authentication; must not be null or blank.
     * @return an instance of {@code HighAvailabilityPgConnection} initialized with the specified credentials.
     */
    default HighAvailabilityPgConnection ofUrl(final String writeUrl,
                                               final String userName,
                                               final String password) {
        return of(ConnectionCredentials.ofUrl(writeUrl, userName, password));
    }

    /**
     * Creates an instance of {@code HighAvailabilityPgConnection} for multiple connection URLs and credentials.
     *
     * @param connectionUrls the collection of connection URLs; must not be null or empty, and all URLs must be valid.
     * @param userName the username for authentication; must not be null or blank.
     * @param password the password for authentication; must not be null or blank.
     * @return an instance of {@code HighAvailabilityPgConnection} initialized with the specified connection URLs and credentials.
     */
    default HighAvailabilityPgConnection ofUrls(final Collection<String> connectionUrls,
                                                final String userName,
                                                final String password) {
        return of(ConnectionCredentials.of(connectionUrls, userName, password));
    }
}
