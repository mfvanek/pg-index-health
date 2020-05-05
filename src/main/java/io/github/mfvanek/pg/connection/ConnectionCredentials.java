/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Parameters for connecting to the database.
 *
 * @author Ivan Vakhrushev
 */
public class ConnectionCredentials {

    private final SortedSet<String> connectionUrls;
    private final String userName;
    private final String password;

    private ConnectionCredentials(@Nonnull final Collection<String> connectionUrls,
                                  @Nonnull final String userName,
                                  @Nonnull final String password) {
        PgConnectionValidators.connectionUrlsNotEmptyAndValid(connectionUrls);
        this.connectionUrls = Collections.unmodifiableSortedSet(new TreeSet<>(connectionUrls));
        this.userName = PgConnectionValidators.userNameNotBlank(userName);
        this.password = PgConnectionValidators.passwordNotBlank(password);
    }

    /**
     * Gets a set of connection strings for accessing all hosts in the database cluster.
     *
     * @return connection urls
     */
    @Nonnull
    public Collection<String> getConnectionUrls() {
        return connectionUrls;
    }

    /**
     * Gets the name of the user to connect to the database.
     *
     * @return the name of the user
     */
    @Nonnull
    public String getUserName() {
        return userName;
    }

    /**
     * Gets the user's password for connecting to the database.
     *
     * @return the user's password
     */
    @Nonnull
    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ConnectionCredentials that = (ConnectionCredentials) o;
        return Objects.equals(connectionUrls, that.connectionUrls) &&
                Objects.equals(userName, that.userName) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionUrls, userName, password);
    }

    @Override
    public String toString() {
        return ConnectionCredentials.class.getSimpleName() + '{' +
                "connectionUrls=" + connectionUrls +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Nonnull
    public static ConnectionCredentials of(@Nonnull final Collection<String> connectionUrls,
                                           @Nonnull final String userName,
                                           @Nonnull final String password) {
        return new ConnectionCredentials(connectionUrls, userName, password);
    }

    @Nonnull
    public static ConnectionCredentials ofUrl(@Nonnull final String writeUrl,
                                              @Nonnull final String userName,
                                              @Nonnull final String password) {
        final Set<String> connectionUrls = Collections.singleton(
                PgConnectionValidators.pgUrlNotBlankAndValid(writeUrl, "writeUrl"));
        return new ConnectionCredentials(connectionUrls, userName, password);
    }
}
