/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Set;

// TODO add and actualize tests
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
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, Set.of());
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToMaster,
                                                  @Nonnull final Set<PgConnection> connectionsToReplicas) {
        return new HighAvailabilityPgConnectionImpl(connectionToMaster, connectionsToReplicas);
    }
}
