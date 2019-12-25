/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
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
