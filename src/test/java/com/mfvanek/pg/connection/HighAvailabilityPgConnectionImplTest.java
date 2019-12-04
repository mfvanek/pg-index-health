/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class HighAvailabilityPgConnectionImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    @Test
    void ofMaster() {
        final var pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final var haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToReplicas(), hasSize(0));
    }
}
