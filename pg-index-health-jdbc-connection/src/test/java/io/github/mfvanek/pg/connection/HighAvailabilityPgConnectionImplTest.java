/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.connection.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import java.util.List;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HighAvailabilityPgConnectionImplTest extends DatabaseAwareTestBase {

    @Test
    void ofPrimary() {
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(getPgConnection());
        assertThat(haPgConnection).isNotNull();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster())
            .isNotNull()
            .hasSize(1)
            .containsExactly(getPgConnection())
            .isUnmodifiable();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster().iterator().next()).isEqualTo(haPgConnection.getConnectionToPrimary());
    }

    @Test
    void shouldBeUnmodifiable() {
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(getPgConnection());
        assertThat(haPgConnection).isNotNull();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster())
            .isNotNull()
            .hasSize(1)
            .containsExactly(getPgConnection())
            .isUnmodifiable();
    }

    @Test
    void withReplicas() {
        final PgConnection primary = getPgConnection();
        final PgConnection replica = getConnectionToReplica();
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(primary, List.of(primary, replica));
        assertThat(haPgConnection).isNotNull();
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster())
            .isNotNull()
            .hasSize(2)
            .containsExactlyInAnyOrder(primary, replica)
            .isUnmodifiable();
    }

    @Test
    void shouldContainsConnectionToPrimary() {
        final PgConnection primary = getPgConnection();
        final PgConnection replica = getConnectionToReplica();
        final List<PgConnection> connectionsOnlyToReplicas = List.of(replica);
        assertThatThrownBy(() -> HighAvailabilityPgConnectionImpl.of(primary, connectionsOnlyToReplicas))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("connectionsToAllHostsInCluster have to contain a connection to the primary");
    }

    @Nonnull
    private PgConnection getConnectionToReplica() {
        return PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl("jdbc:postgresql://replica:5432"));
    }
}
