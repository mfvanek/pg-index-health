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

import io.github.mfvanek.pg.connection.host.PgUrlValidators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Immutable parameters for connecting to the database.
 *
 * @author Ivan Vakhrushev
 */
public final class ConnectionCredentials {

    private final SortedSet<String> connectionUrls;
    private final String userName;
    private final String password;

    private ConnectionCredentials(final Collection<String> connectionUrls,
                                  final String userName,
                                  final String password) {
        final List<String> defensiveCopy = new ArrayList<>(Objects.requireNonNull(connectionUrls, "connectionUrls cannot be null"));
        PgConnectionValidators.connectionUrlsNotEmptyAndValid(defensiveCopy);
        this.connectionUrls = Collections.unmodifiableSortedSet(new TreeSet<>(defensiveCopy));
        this.userName = PgConnectionValidators.userNameNotBlank(userName);
        this.password = PgConnectionValidators.passwordNotBlank(password);
    }

    /**
     * Retrieves a set of connection strings for accessing all hosts in the database cluster.
     *
     * @return connection urls
     */
    public Collection<String> getConnectionUrls() {
        return connectionUrls;
    }

    /**
     * Retrieves the name of the user to connect to the database.
     *
     * @return the name of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Retrieves the user's password for connecting to the database.
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final ConnectionCredentials that)) {
            return false;
        }

        return Objects.equals(connectionUrls, that.connectionUrls) &&
            Objects.equals(userName, that.userName) &&
            Objects.equals(password, that.password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(connectionUrls, userName, password);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return ConnectionCredentials.class.getSimpleName() + '{' +
            "connectionUrls=" + connectionUrls +
            ", userName='" + userName + '\'' +
            ", password='" + password + '\'' +
            '}';
    }

    /**
     * Creates a {@link ConnectionCredentials} object with multiple connection URLs.
     *
     * @param connectionUrls the collection of connection URLs; must not be null or empty, and all URLs must be valid.
     * @param userName       the username for authentication; must not be null or blank.
     * @param password       the password for authentication; must not be null or blank.
     * @return a new {@link ConnectionCredentials} instance.
     */
    public static ConnectionCredentials of(final Collection<String> connectionUrls,
                                           final String userName,
                                           final String password) {
        return new ConnectionCredentials(connectionUrls, userName, password);
    }

    /**
     * Creates a {@link ConnectionCredentials} object for a single connection URL.
     *
     * @param writeUrl the write connection URL; must not be null, blank, or have an invalid format.
     * @param userName the username for authentication; must not be null or blank.
     * @param password the password for authentication; must not be null or blank.
     * @return a new {@link ConnectionCredentials} instance containing the validated URL.
     */
    public static ConnectionCredentials ofUrl(final String writeUrl,
                                              final String userName,
                                              final String password) {
        final Set<String> connectionUrls = Set.of(PgUrlValidators.pgUrlNotBlankAndValid(writeUrl, "writeUrl"));
        return new ConnectionCredentials(connectionUrls, userName, password);
    }
}
