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

import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HighAvailabilityPgConnectionImplTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    @Test
    void ofPrimary() {
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(1));
        assertEquals(haPgConnection.getConnectionToPrimary(), haPgConnection.getConnectionsToAllHostsInCluster().iterator().next());
    }

    @Test
    void shouldBeUnmodifiable() {
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertNotNull(haPgConnection);
        assertThrows(UnsupportedOperationException.class, () -> haPgConnection.getConnectionsToAllHostsInCluster().clear());
    }

    @Test
    void withReplicas() {
        final PgConnection primary = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection replica = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(),
                PgHostImpl.ofName("replica"));
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(primary,
                new HashSet<>(Arrays.asList(primary, replica)));
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(2));
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), containsInAnyOrder(primary, replica));
    }
}
