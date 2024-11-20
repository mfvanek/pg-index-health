/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.host;

import javax.annotation.Nonnull;

/**
 * An abstraction of database host.
 * <p>
 * This is a real server where your queries will be executed.
 * </p>
 *
 * @author Ivan Vakhrushev
 */
public interface PgHost {

    /**
     * Retrieves a valid connection string to this host.
     *
     * @return connection url to the host
     */
    @Nonnull
    String getPgUrl();

    /**
     * Retrieves the name of this host.
     *
     * @return host name
     */
    @Nonnull
    String getName();

    /**
     * Retrieves the port of this host.
     *
     * @return port
     */
    int getPort();

    /**
     * Determines whether this host can be a primary host.
     *
     * @return {@code true} if this host can be a primary host
     */
    boolean canBePrimary();

    /**
     * Determines whether this host cannot act as a primary host.
     *
     * @return {@code true} if this host cannot be a primary host; {@code false} otherwise.
     */
    default boolean cannotBePrimary() {
        return !canBePrimary();
    }
}
