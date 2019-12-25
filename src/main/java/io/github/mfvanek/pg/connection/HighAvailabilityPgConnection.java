/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * An abstraction of connection to high availability cluster (with set of master host and replicas).
 */
public interface HighAvailabilityPgConnection {

    @Nonnull
    PgConnection getConnectionToMaster();

    @Nonnull
    Set<PgConnection> getConnectionsToReplicas();
}
