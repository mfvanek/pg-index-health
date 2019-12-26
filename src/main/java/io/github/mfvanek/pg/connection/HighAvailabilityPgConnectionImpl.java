/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private final PgConnection connectionToMaster;
    private final Set<PgConnection> connectionsToReplicas;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToMaster,
                                             @Nonnull final Set<PgConnection> connectionsToReplicas) {
        this.connectionToMaster = Objects.requireNonNull(connectionToMaster);
        this.connectionsToReplicas = Objects.requireNonNull(connectionsToReplicas);
    }

    @Override
    @Nonnull
    public PgConnection getConnectionToMaster() {
        return connectionToMaster;
    }

    @Override
    @Nonnull
    public Set<PgConnection> getConnectionsToReplicas() {
        return connectionsToReplicas;
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToMaster) {
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, Collections.singleton(connectionToMaster));
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToMaster,
                                                  @Nonnull final Set<PgConnection> connectionsToReplicas) {
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, connectionsToReplicas);
    }
}
