/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
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
        final PgConnection pgConnection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToReplicas(), hasSize(1));
        assertEquals(haPgConnection.getConnectionToMaster(), haPgConnection.getConnectionsToReplicas().iterator().next());
    }

    @Test
    void withReplicas() {
        final PgConnection master = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        final PgConnection replica = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("replica"));
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(master, Set.of(master, replica));
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToReplicas(), hasSize(2));
        assertThat(haPgConnection.getConnectionsToReplicas(), Matchers.containsInAnyOrder(master, replica));
    }
}
