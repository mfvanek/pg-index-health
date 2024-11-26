/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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
import javax.annotation.Nonnull;

public interface HighAvailabilityPgConnectionFactory {

    /**
     * Creates instance of {@code HighAvailabilityPgConnection} with given credentials.
     *
     * @param credentials given credentials.
     * @return instance of {@code HighAvailabilityPgConnection}
     */
    @Nonnull
    HighAvailabilityPgConnection of(@Nonnull ConnectionCredentials credentials);

    @Nonnull
    default HighAvailabilityPgConnection ofUrl(@Nonnull final String writeUrl,
                                               @Nonnull final String userName,
                                               @Nonnull final String password) {
        return of(ConnectionCredentials.ofUrl(writeUrl, userName, password));
    }

    @Nonnull
    default HighAvailabilityPgConnection ofUrls(@Nonnull final Collection<String> connectionUrls,
                                                @Nonnull final String userName,
                                                @Nonnull final String password) {
        return of(ConnectionCredentials.of(connectionUrls, userName, password));
    }
}
