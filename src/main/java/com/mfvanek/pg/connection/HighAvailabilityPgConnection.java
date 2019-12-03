/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import java.util.Set;

public interface HighAvailabilityPgConnection {

    @Nonnull
    PgConnection getConnectionToMaster();

    @Nonnull
    Set<PgConnection> getConnectionsToReplicas();
}
