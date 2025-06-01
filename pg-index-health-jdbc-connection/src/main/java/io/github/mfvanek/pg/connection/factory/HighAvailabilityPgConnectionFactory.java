/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;

import java.util.Collection;

public interface HighAvailabilityPgConnectionFactory {

    /**
     * Creates instance of {@code HighAvailabilityPgConnection} with given credentials.
     *
     * @param credentials given credentials.
     * @return instance of {@code HighAvailabilityPgConnection}
     */
    HighAvailabilityPgConnection of(ConnectionCredentials credentials);

    default HighAvailabilityPgConnection ofUrl(final String writeUrl,
                                               final String userName,
                                               final String password) {
        return of(ConnectionCredentials.ofUrl(writeUrl, userName, password));
    }

    default HighAvailabilityPgConnection ofUrls(final Collection<String> connectionUrls,
                                                final String userName,
                                                final String password) {
        return of(ConnectionCredentials.of(connectionUrls, userName, password));
    }
}
