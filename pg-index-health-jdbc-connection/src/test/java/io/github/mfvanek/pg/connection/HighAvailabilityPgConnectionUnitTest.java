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

import ch.qos.logback.classic.Level;
import io.github.mfvanek.pg.testing.LogsCaptor;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.List;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;

@Tag("fast")
class HighAvailabilityPgConnectionUnitTest {

    private final ConnectionMocks firstConnectionMocks = new ConnectionMocks();
    private final ConnectionMocks secondConnectionMocks = new ConnectionMocks();

    @Test
    void cachedPrimaryWillBeReturnedIfThereIsNoPrimaryUpdate() throws SQLException {
        initMocks(firstConnectionMocks, true);
        initMocks(secondConnectionMocks, false);

        final List<PgConnection> pgConnections = prepareConnections();
        final HighAvailabilityPgConnection haPgConnection = HighAvailabilityPgConnectionImpl.of(pgConnections.get(0), pgConnections, 400L);

        assertThat(haPgConnection.getConnectionToPrimary())
                .as("First connection is primary")
                .isEqualTo(pgConnections.get(0))
                .as("Second connection is not primary")
                .isNotEqualTo(pgConnections.get(1));
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
                .isEqualTo(pgConnections.get(0));

        Mockito.when(secondConnectionMocks.resultSet.getBoolean(1)).thenReturn(true);
        Awaitility
                .await()
                .atMost(Duration.ofMillis(1000L))
                .pollDelay(Duration.ofMillis(500L))
                .until(() -> true);
        assertThat(haPgConnection.getConnectionToPrimary())
                .as("Second connection become new primary")
                .isEqualTo(pgConnections.get(1));
    }

    @Test
    void primaryUpdaterExpectedToExecuteTenTimes() throws SQLException {
        initMocks(firstConnectionMocks, false);
        initMocks(secondConnectionMocks, false);

        final List<PgConnection> pgConnections = prepareConnections();
        HighAvailabilityPgConnectionImpl.of(pgConnections.get(0), pgConnections, 50L);

        Awaitility
                .await()
                .pollDelay(Duration.ofMillis(650)) // start delay compensation + OS dependent behavior
                .until(() -> true);

        // Due to interleaving method may be called more than 10 times but not less than 10
        Mockito.verify(firstConnectionMocks.dataSource, Mockito.atLeast(10)).getConnection();
    }

    @Test
    void updateConnectionToPrimaryShouldCatchAndLogExceptions() throws SQLException {
        try (LogsCaptor logsCaptor = new LogsCaptor(HighAvailabilityPgConnectionImpl.class, Level.WARN)) {
            initMocksCommon(firstConnectionMocks);
            Mockito.when(firstConnectionMocks.resultSet.getBoolean(1)).thenThrow(RuntimeException.class);
            initMocksCommon(secondConnectionMocks);
            Mockito.when(secondConnectionMocks.resultSet.getBoolean(1)).thenThrow(RuntimeException.class);

            final List<PgConnection> pgConnections = prepareConnections();
            HighAvailabilityPgConnectionImpl.of(pgConnections.get(0), pgConnections, 10L);

            Awaitility
                    .await()
                    .pollDelay(Duration.ofMillis(120)) // start delay compensation + OS dependent behavior
                    .until(() -> true);

            assertThat(logsCaptor.getLogs())
                    .hasSizeGreaterThanOrEqualTo(10)
                    .allMatch(l -> l.getMessage().contains("Exception during primary detection for host"));
        }
    }

    private void initMocks(final ConnectionMocks connectionMocks, final boolean resultSetBooleanValue) throws SQLException {
        initMocksCommon(connectionMocks);
        Mockito.when(connectionMocks.resultSet.getBoolean(1)).thenReturn(resultSetBooleanValue);
    }

    private void initMocksCommon(final ConnectionMocks connectionMocks) throws SQLException {
        Mockito.when(connectionMocks.dataSource.getConnection()).thenReturn(connectionMocks.connection);
        Mockito.when(connectionMocks.connection.createStatement()).thenReturn(connectionMocks.statement);
        Mockito.when(connectionMocks.resultSet.next()).thenReturn(true);
        Mockito.when(connectionMocks.statement.executeQuery(anyString())).thenReturn(connectionMocks.resultSet);
    }

    @Nonnull
    private List<PgConnection> prepareConnections() {
        final PgHost localhostOne = PgHostImpl.ofName("localhost-1");
        final PgHost localhostTwo = PgHostImpl.ofName("localhost-2");
        final PgConnection firstConnection = PgConnectionImpl.of(firstConnectionMocks.dataSource, localhostOne);
        final PgConnection secondConnection = PgConnectionImpl.of(secondConnectionMocks.dataSource, localhostTwo);
        return List.of(firstConnection, secondConnection);
    }

    private static final class ConnectionMocks {

        private final Connection connection = Mockito.mock(Connection.class);
        private final Statement statement = Mockito.mock(Statement.class);
        private final ResultSet resultSet = Mockito.mock(ResultSet.class);
        private final DataSource dataSource = Mockito.mock(DataSource.class);
    }
}
