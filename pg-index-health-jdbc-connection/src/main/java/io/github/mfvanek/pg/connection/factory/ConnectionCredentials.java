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

import io.github.mfvanek.pg.connection.validation.PgConnectionValidators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Parameters for connecting to the database.
 *
 * @author Ivan Vakhrushev
 */
@Immutable
public final class ConnectionCredentials {

    private final SortedSet<String> connectionUrls;
    private final String userName;
    private final String password;

    private ConnectionCredentials(@Nonnull final Collection<String> connectionUrls,
                                  @Nonnull final String userName,
                                  @Nonnull final String password) {
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
    @Nonnull
    public Collection<String> getConnectionUrls() {
        return connectionUrls;
    }

    /**
     * Retrieves the name of the user to connect to the database.
     *
     * @return the name of the user
     */
    @Nonnull
    public String getUserName() {
        return userName;
    }

    /**
     * Retrieves the user's password for connecting to the database.
     *
     * @return the user's password
     */
    @Nonnull
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

        if (!(other instanceof ConnectionCredentials)) {
            return false;
        }

        final ConnectionCredentials that = (ConnectionCredentials) other;
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
    @Nonnull
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
    @Nonnull
    public static ConnectionCredentials of(@Nonnull final Collection<String> connectionUrls,
                                           @Nonnull final String userName,
                                           @Nonnull final String password) {
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
    @Nonnull
    public static ConnectionCredentials ofUrl(@Nonnull final String writeUrl,
                                              @Nonnull final String userName,
                                              @Nonnull final String password) {
        final Set<String> connectionUrls = Set.of(PgConnectionValidators.pgUrlNotBlankAndValid(writeUrl, "writeUrl"));
        return new ConnectionCredentials(connectionUrls, userName, password);
    }
}
