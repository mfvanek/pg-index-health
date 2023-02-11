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

import java.util.Set;
import javax.annotation.Nonnull;

/**
 * An abstraction of a connection to a high availability cluster (with set of primary host and replicas).
 *
 * @author Ivan Vakhrushev
 * @see PgConnection
 */
public interface HighAvailabilityPgConnection {

    /**
     * Gets connection to a primary host in the cluster.
     *
     * @return {@code PgConnection} to a primary host in the cluster
     */
    @Nonnull
    PgConnection getConnectionToPrimary();

    /**
     * Gets connections to all hosts in the cluster (including a connection to a primary host).
     *
     * @return {@code Set} of connections to all hosts in target cluster
     */
    @Nonnull
    Set<PgConnection> getConnectionsToAllHostsInCluster();
}
