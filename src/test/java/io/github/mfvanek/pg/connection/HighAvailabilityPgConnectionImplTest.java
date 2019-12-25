/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertThat(haPgConnection.getConnectionsToReplicas(), hasSize(1));
        assertEquals(haPgConnection.getConnectionToMaster(), haPgConnection.getConnectionsToReplicas().iterator().next());
    }

    @Test
    void withReplicas() {
        final var master = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final var replica = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("replica"));
        final var haPgConnection = HighAvailabilityPgConnectionImpl.of(master, Set.of(master, replica));
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToReplicas(), hasSize(2));
        assertThat(haPgConnection.getConnectionsToReplicas(), Matchers.containsInAnyOrder(master, replica));
    }
}
