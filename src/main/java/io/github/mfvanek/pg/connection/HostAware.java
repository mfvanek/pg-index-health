/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;

/**
 * Allows to get information about database host.
 *
 * @author Ivan Vakhrushev
 */
public interface HostAware {

    /**
     * Gets information about host in the cluster.
     *
     * @return {@code PgHost}
     * @see PgHost
     */
    @Nonnull
    PgHost getHost();
}
