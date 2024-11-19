/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.validation;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.host.PgUrlParser;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Utility class providing validation methods for PostgreSQL connection parameters.
 * This class includes methods for validating PostgreSQL URLs, usernames, passwords,
 * hostnames, ports, and ensuring connections meet specific requirements.
 */
public final class PgConnectionValidators {

    private PgConnectionValidators() {
        throw new UnsupportedOperationException();
    }

    /**
     * Validates that a PostgreSQL URL is not blank and has a valid format.
     *
     * @param pgUrl        the PostgreSQL URL to validate; must not be {@code null} or blank.
     * @param argumentName the name of the argument being validated, for use in error messages.
     * @return the validated PostgreSQL URL.
     * @throws IllegalArgumentException if the URL is blank or does not start with the expected header.
     */
    @Nonnull
    public static String pgUrlNotBlankAndValid(@Nonnull final String pgUrl, @Nonnull final String argumentName) {
        notBlank(pgUrl, argumentName);
        if (!Objects.requireNonNull(pgUrl).startsWith(PgUrlParser.URL_HEADER)) {
            throw new IllegalArgumentException(argumentName + " has invalid format");
        }
        return pgUrl;
    }

    /**
     * Validates that a username is not blank.
     *
     * @param userName the username to validate; must not be {@code null} or blank.
     * @return the validated username.
     * @throws IllegalArgumentException if the username is blank.
     */
    @Nonnull
    public static String userNameNotBlank(@Nonnull final String userName) {
        notBlank(userName, "userName");
        return userName;
    }

    /**
     * Validates that a password is not blank.
     *
     * @param password the password to validate; must not be {@code null} or blank.
     * @return the validated password.
     * @throws IllegalArgumentException if the password is blank.
     */
    @Nonnull
    public static String passwordNotBlank(@Nonnull final String password) {
        notBlank(password, "password");
        return password;
    }

    /**
     * Validates that a collection of PostgreSQL connection URLs is not empty and that each URL is valid.
     *
     * @param connectionUrls the collection of connection URLs; must not be {@code null} or empty.
     * @throws IllegalArgumentException if the collection is empty or any URL is invalid.
     */
    public static void connectionUrlsNotEmptyAndValid(@Nonnull final Collection<String> connectionUrls) {
        Objects.requireNonNull(connectionUrls, "connectionUrls");
        if (connectionUrls.isEmpty()) {
            throw new IllegalArgumentException("connectionUrls have to contain at least one url");
        }
        connectionUrls.forEach(url -> pgUrlNotBlankAndValid(url, "connectionUrl"));
    }

    /**
     * Validates that the provided connection to the primary is included in the set of all connections to hosts in the cluster.
     *
     * @param connectionToPrimary            the connection to the primary host; must not be {@code null}.
     * @param connectionsToAllHostsInCluster the set of connections to all hosts in the cluster; must not be {@code null}.
     * @throws IllegalArgumentException if the primary connection is not included in the set of connections.
     */
    public static void shouldContainsConnectionToPrimary(@Nonnull final PgConnection connectionToPrimary,
                                                         @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        if (!connectionsToAllHostsInCluster.contains(connectionToPrimary)) {
            throw new IllegalArgumentException("connectionsToAllHostsInCluster have to contain a connection to the primary");
        }
    }

    /**
     * Validates that a port number is within the acceptable range (1024–65535).
     *
     * @param port the port number to validate.
     * @return the validated port number.
     * @throws IllegalArgumentException if the port number is outside the acceptable range.
     */
    public static int portInAcceptableRange(final int port) {
        // https://en.wikipedia.org/wiki/List_of_TCP_and_UDP_port_numbers
        if (port < 1024 || port > 65_535) {
            throw new IllegalArgumentException("the port number must be in the range from 1024 to 65535");
        }
        return port;
    }

    /**
     * Validates that a hostname is not blank.
     *
     * @param hostName the hostname to validate; must not be {@code null} or blank.
     * @return the validated hostname.
     * @throws IllegalArgumentException if the hostname is blank.
     */
    @Nonnull
    public static String hostNameNotBlank(@Nonnull final String hostName) {
        notBlank(hostName, "hostName");
        return hostName;
    }

    /**
     * Validates that a string argument is not blank.
     *
     * @param argumentValue the value to validate; must not be {@code null} or blank.
     * @param argumentName  the name of the argument being validated, for use in error messages.
     * @throws IllegalArgumentException if the value is {@code null}, blank, or empty.
     */
    private static void notBlank(@Nonnull final String argumentValue, @Nonnull final String argumentName) {
        if (Objects.requireNonNull(argumentValue, argumentName + " cannot be null").isBlank()) {
            throw new IllegalArgumentException(argumentName + " cannot be blank or empty");
        }
    }
}
