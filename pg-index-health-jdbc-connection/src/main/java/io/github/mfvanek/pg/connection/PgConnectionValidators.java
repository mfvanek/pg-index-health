/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

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

    static int portInAcceptableRange(final int port) {
        // https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
        if (port < 1024 || port > 65_535) {
            throw new IllegalArgumentException("the port number must be in the range from 1024 to 65535");
        }
        return port;
    }

    @Nonnull
    static String hostNameNotBlank(@Nonnull final String hostName) {
        notBlank(hostName, "hostName");
        return hostName;
    }

    private static void notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (Objects.requireNonNull(argumentValue, argumentName + " cannot be null").isBlank()) {
            throw new IllegalArgumentException(argumentName + " cannot be blank or empty");
        }
    }
}
