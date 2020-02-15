/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private final PgConnection connectionToMaster;
    private final Set<PgConnection> connectionsToAllHostsInCluster;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToMaster,
                                             @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        this.connectionToMaster = Objects.requireNonNull(connectionToMaster);
        this.connectionsToAllHostsInCluster = Objects.requireNonNull(connectionsToAllHostsInCluster);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgConnection getConnectionToMaster() {
        return connectionToMaster;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<PgConnection> getConnectionsToAllHostsInCluster() {
        return Collections.unmodifiableSet(connectionsToAllHostsInCluster);
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToMaster) {
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, Collections.singleton(connectionToMaster));
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToMaster,
                                                  @Nonnull final Set<PgConnection> connectionsToAllHostsInCluster) {
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, connectionsToAllHostsInCluster);
    }
}
