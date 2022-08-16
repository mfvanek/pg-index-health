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

import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        final PgConnection replica = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofName("replica"));
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(primary, Arrays.asList(primary, replica));
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
        final PgConnection replica = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofName("replica"));
        final List<PgConnection> connectionsOnlyToReplicas = Collections.singletonList(replica);
        assertThatThrownBy(() -> HighAvailabilityPgConnectionImpl.of(primary, connectionsOnlyToReplicas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("connectionsToAllHostsInCluster have to contain a connection to the primary");
    }
}
