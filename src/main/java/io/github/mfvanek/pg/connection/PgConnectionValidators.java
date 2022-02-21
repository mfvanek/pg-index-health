/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

final class PgConnectionValidators {

    private PgConnectionValidators() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    static String pgUrlNotBlankAndValid(@Nonnull final String pgUrl, @Nonnull final String argumentName) {
        notBlank(pgUrl, argumentName);
        if (!Objects.requireNonNull(pgUrl).startsWith("jdbc:postgresql://")) {
            throw new IllegalArgumentException(argumentName + " has invalid format");
        }
        return pgUrl;
    }

    @Nonnull
    static String userNameNotBlank(@Nonnull final String userName) {
        notBlank(userName, "userName");
        return userName;
    }

    @Nonnull
    static String passwordNotBlank(@Nonnull final String password) {
        notBlank(password, "password");
        return password;
    }

    static void connectionUrlsNotEmptyAndValid(@Nonnull final Collection<String> connectionUrls) {
        Objects.requireNonNull(connectionUrls, "connectionUrls");
        if (connectionUrls.isEmpty()) {
            throw new IllegalArgumentException("connectionUrls have to contain at least one url");
        }
        connectionUrls.forEach(url -> pgUrlNotBlankAndValid(url, "connectionUrl"));
    }

    static void shouldContainsConnectionToPrimary(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        if (!connectionsToAllHostsInCluster.contains(connectionToPrimary)) {
            throw new IllegalArgumentException("connectionsToAllHostsInCluster have to contain a connection to the primary");
        }
    }

    private static void notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (StringUtils.isBlank(Objects.requireNonNull(argumentValue, argumentName + " cannot be null"))) {
            throw new IllegalArgumentException(argumentName);
        }
    }
}
