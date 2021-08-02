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

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private final PgConnection connectionToPrimary;
    private final Set<PgConnection> connectionsToAllHostsInCluster;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToPrimary,
                                             @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        this.connectionToPrimary = Objects.requireNonNull(connectionToPrimary, "connectionToPrimary");
        this.connectionsToAllHostsInCluster = Collections.unmodifiableSet(
                Objects.requireNonNull(connectionsToAllHostsInCluster));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgConnection getConnectionToPrimary() {
        return connectionToPrimary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<PgConnection> getConnectionsToAllHostsInCluster() {
        return connectionsToAllHostsInCluster;
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary) {
        return new HighAvailabilityPgConnectionImpl(connectionToPrimary, Collections.singleton(connectionToPrimary));
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        return new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster);
    }
}
