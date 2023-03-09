/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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
import org.mockito.Mockito;

import java.util.List;
import javax.sql.DataSource;

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
        final PgConnection replica = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofName("replica"));
        final List<PgConnection> connectionsOnlyToReplicas = List.of(replica);
        assertThatThrownBy(() -> HighAvailabilityPgConnectionImpl.of(primary, connectionsOnlyToReplicas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("connectionsToAllHostsInCluster have to contain a connection to the primary");
    }

    @Test
    void highAvailabilityConnectionConsiderTwoDifferentConnectionsAsTheSame() {
        final DataSource firstDatasource = Mockito.mock(DataSource.class);
        final DataSource secondDatasource = Mockito.mock(DataSource.class);
        final PgConnection firstPgConnection = PgConnectionImpl.of(firstDatasource, PgHostImpl.ofUrl("jdbc:postgresql://localhost:5432"));
        final PgConnection secondPgConnection = PgConnectionImpl.of(secondDatasource, PgHostImpl.ofUrl("jdbc:postgresql://localhost:5431"));

        final HighAvailabilityPgConnection highAvailabilityConnection = HighAvailabilityPgConnectionImpl.of(firstPgConnection, List.of(firstPgConnection, secondPgConnection));

        /*
        * Two different connections should start primary host determiner in hapgconnection. But this won't happen because of the equals implementation of PgHost
         */
        assertThat(highAvailabilityConnection.getConnectionsToAllHostsInCluster()).hasSize(1);
    }
}
