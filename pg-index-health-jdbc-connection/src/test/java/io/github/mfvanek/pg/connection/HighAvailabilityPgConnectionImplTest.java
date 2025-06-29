/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.connection.support.DatabaseAwareTestBase;
import org.awaitility.Awaitility;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.logging.Level;

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
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster().iterator().next())
            .isEqualTo(haPgConnection.getConnectionToPrimary());
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
        try (LogsCaptor ignored = new LogsCaptor(HighAvailabilityPgConnectionImpl.class, Level.FINEST)) {
            final PgConnection primary = getPgConnection();
            final PgConnection replica = getConnectionToReplica();
            final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(primary, List.of(primary, replica), 5L);
            assertThat(haPgConnection).isNotNull();
            Awaitility
                .await()
                .atMost(Duration.ofMillis(100L))
                .pollDelay(Duration.ofMillis(20L))
                .until(() -> Boolean.TRUE);
            assertThat(haPgConnection.getConnectionsToAllHostsInCluster())
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(primary, replica)
                .isUnmodifiable();
        }
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

    @NonNull
    private PgConnection getConnectionToReplica() {
        return PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl("jdbc:postgresql://replica:5432"));
    }
}
