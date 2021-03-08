/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

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
     * Gets a valid connection string to this host.
     *
     * @return connection url to the host
     */
    @Nonnull
    String getPgUrl();

    /**
     * Gets the name of this host.
     *
     * @return host name
     */
    @Nonnull
    String getName();

    /**
     * Determines whether this host can be a primary host.
     *
     * @return {@code true} if this host can be a primary host
     */
    boolean canBePrimary();

    default boolean cannotBePrimary() {
        return !canBePrimary();
    }
}
