/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    // TODO Bad design. Need logic here to deal with failover/switch-over in real cluster.
    //  As a possible solution - cache connectionToPrimary for a short period of time (e.g. 1 minute)
    private final PgConnection connectionToPrimary;
    private final Set<PgConnection> connectionsToAllHostsInCluster;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToPrimary,
                                             @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster) {
        this.connectionToPrimary = Objects.requireNonNull(connectionToPrimary, "connectionToPrimary");
        final Set<PgConnection> defensiveCopy = new HashSet<>(
                Objects.requireNonNull(connectionsToAllHostsInCluster, "connectionsToAllHostsInCluster"));
        PgConnectionValidators.shouldContainsConnectionToPrimary(connectionToPrimary, defensiveCopy);
        this.connectionsToAllHostsInCluster = Collections.unmodifiableSet(defensiveCopy);
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
                                                  @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster) {
        return new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster);
    }
}
