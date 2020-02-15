/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An abstraction of a connection to a high availability cluster (with set of master host and replicas).
 *
 * @author Ivan Vakhrushev
 * @see PgConnection
 */
public interface HighAvailabilityPgConnection {

    /**
     * Gets connection to a master host in target cluster.
     *
     * @return {@code PgConnection} to a master host in target cluster
     */
    @Nonnull
    PgConnection getConnectionToMaster();

    /**
     * Gets connections to all hosts in target cluster (including a connection to a master host).
     *
     * @return {@code Set} of connections to all hosts in target cluster
     */
    @Nonnull
    Set<PgConnection> getConnectionsToAllHostsInCluster();
}
