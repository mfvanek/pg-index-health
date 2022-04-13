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
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HighAvailabilityPgConnectionImplTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    @Test
    void ofPrimary() {
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertThat(haPgConnection).isNotNull();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster()).hasSize(1);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster().iterator().next()).isEqualTo(haPgConnection.getConnectionToPrimary());
    }

    @Test
    void shouldBeUnmodifiable() {
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnection);
        assertThat(haPgConnection).isNotNull();
        assertThatThrownBy(() -> haPgConnection.getConnectionsToAllHostsInCluster().clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void withReplicas() {
        final PgConnection primary = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection replica = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(),
                PgHostImpl.ofName("replica"));
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(primary, Arrays.asList(primary, replica));
        assertThat(haPgConnection).isNotNull();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster()).hasSize(2);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster()).containsExactlyInAnyOrder(primary, replica);
    }

    @Test
    void shouldContainsConnectionToPrimary() {
        final PgConnection primary = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection replica = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(),
                PgHostImpl.ofName("replica"));
        assertThatThrownBy(() -> HighAvailabilityPgConnectionImpl.of(primary, Collections.singletonList(replica)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("connectionsToAllHostsInCluster have to contain a connection to the primary");
    }
}
