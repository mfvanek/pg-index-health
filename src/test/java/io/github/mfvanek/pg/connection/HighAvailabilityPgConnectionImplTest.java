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
import org.awaitility.Awaitility;
import org.graalvm.compiler.core.common.SuppressFBWarnings;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;

class HighAvailabilityPgConnectionImplTest extends DatabaseAwareTestBase {

    private final ConnectionMocks firstConnectionMocks = new ConnectionMocks();
    private final ConnectionMocks secondConnectionMocks = new ConnectionMocks();

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

    @Test
    void cachedPrimaryWillBeReturnedIfThereIsNoPrimaryUpdate() throws SQLException {
        initMocks(firstConnectionMocks, true);
        initMocks(secondConnectionMocks, false);

        final PgHost localhostOne = PgHostImpl.ofName("localhost-1");
        final PgHost localhostTwo = PgHostImpl.ofName("localhost-2");
        final PgConnection firstConnection = PgConnectionImpl.of(firstConnectionMocks.dataSource, localhostOne);
        final PgConnection secondConnection = PgConnectionImpl.of(secondConnectionMocks.dataSource, localhostTwo);
        final List<PgConnection> pgConnections = Arrays.asList(firstConnection, secondConnection);
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections, 400L);

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is primary")
                .isEqualTo(firstConnection)
                .as("Second connection is not primary")
                .isNotEqualTo(secondConnection);
        Awaitility
                .await()
                .atMost(Duration.ofMillis(1000L))
                .pollDelay(Duration.ofMillis(500L))
                .until(() -> true);

        Mockito.when(firstConnectionMocks.resultSet.getBoolean(1)).thenReturn(false);
        Awaitility
                .await()
                .atMost(Duration.ofMillis(1000L))
                .pollDelay(Duration.ofMillis(500L))
                .until(() -> true);
        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Without new primary first connection considered as primary")
                .isEqualTo(firstConnection);

        Mockito.when(secondConnectionMocks.resultSet.getBoolean(1)).thenReturn(true);
        Awaitility
                .await()
                .atMost(Duration.ofMillis(1000L))
                .pollDelay(Duration.ofMillis(500L))
                .until(() -> true);
        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Second connection become new primary")
                .isEqualTo(secondConnection);
    }

    @Test
    @SuppressFBWarnings(
            value = {"OBL_UNSATISFIED_OBLIGATION", "ODR_OPEN_DATABASE_RESOURCE"},
            justification = "False positive on mockito verify returning unused value")
    void primaryUpdaterExpectedToExecuteTenTimes() throws SQLException {
        initMocks(firstConnectionMocks, false);
        initMocks(secondConnectionMocks, false);

        final PgHost localhostOne = PgHostImpl.ofName("localhost-1");
        final PgHost localhostTwo = PgHostImpl.ofName("localhost-2");
        final PgConnection firstConnection = PgConnectionImpl.of(firstConnectionMocks.dataSource, localhostOne);
        final PgConnection secondConnection = PgConnectionImpl.of(secondConnectionMocks.dataSource, localhostTwo);
        final List<PgConnection> pgConnections = Arrays.asList(firstConnection, secondConnection);
        HighAvailabilityPgConnectionImpl.of(firstConnection, pgConnections, 100L);

        Awaitility
                .await()
                .pollDelay(Duration.ofMillis(1100)) // 100ms start delay compensation
                .until(() -> true);

        // Due to interleaving method may be called more than 10 times but not less than 10
        Mockito.verify(firstConnectionMocks.dataSource, Mockito.atLeast(10)).getConnection();

    }

    @SuppressFBWarnings(
            value = {"OBL_UNSATISFIED_OBLIGATION", "ODR_OPEN_DATABASE_RESOURCE"},
            justification = "False positive on mockito verify returning unused value")
    private void initMocks(final ConnectionMocks connectionMocks, final boolean resultSetBooleanValue) throws SQLException {
        Mockito.when(connectionMocks.dataSource.getConnection()).thenReturn(connectionMocks.connection);
        Mockito.when(connectionMocks.connection.createStatement()).thenReturn(connectionMocks.statement);
        Mockito.when(connectionMocks.resultSet.next()).thenReturn(true);
        Mockito.when(connectionMocks.resultSet.getBoolean(1)).thenReturn(resultSetBooleanValue);
        Mockito.when(connectionMocks.statement.executeQuery(anyString())).thenReturn(connectionMocks.resultSet);
    }

    private static class ConnectionMocks {

        private final Connection connection = Mockito.mock(Connection.class);
        private final Statement statement = Mockito.mock(Statement.class);
        private final ResultSet resultSet = Mockito.mock(ResultSet.class);
        private final DataSource dataSource = Mockito.mock(DataSource.class);
    }
}
