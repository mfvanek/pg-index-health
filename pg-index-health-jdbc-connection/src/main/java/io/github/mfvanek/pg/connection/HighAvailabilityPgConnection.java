/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.Set;

/**
 * An abstraction of a connection to a high availability cluster (with set of primary host and replicas).
 *
 * @author Ivan Vakhrushev
 * @see PgConnection
 */
public interface HighAvailabilityPgConnection {

    /**
     * Retrieves connection to a primary host in the cluster.
     *
     * @return {@code PgConnection} to a primary host in the cluster
     */
    PgConnection getConnectionToPrimary();

    /**
     * Retrieves connections to all hosts in the cluster (including a connection to a primary host).
     *
     * @return {@code Set} of connections to all hosts in target cluster
     */
    Set<PgConnection> getConnectionsToAllHostsInCluster();
}
