/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.core.statistics.StatisticsMaintenanceOnHost;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Set;
import java.util.function.Function;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class DatabaseManagementImplUnitTest {

    private final HighAvailabilityPgConnection haPgConnectionMock = Mockito.mock(HighAvailabilityPgConnection.class);

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionWhenInvalidArgumentsArePassed() {
        assertThatThrownBy(() -> new DatabaseManagementImpl(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("haPgConnection cannot be null");

        assertThatThrownBy(() -> new DatabaseManagementImpl(haPgConnectionMock, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("statisticsOnHostFactory cannot be null");
    }

    @Test
    void resetStatisticsShouldAggregateResultsFromAllHosts() {
        final StatisticsMaintenanceOnHost firstStatisticsMock = Mockito.mock(StatisticsMaintenanceOnHost.class);
        final StatisticsMaintenanceOnHost secondStatisticsMock = Mockito.mock(StatisticsMaintenanceOnHost.class);
        final DataSource dataSourceMock = Mockito.mock(DataSource.class);
        final PgConnection firstConnection = PgConnectionImpl.of(dataSourceMock, PgHostImpl.ofUrl("jdbc:postgresql://primary:6432"));
        final PgConnection secondConnection = PgConnectionImpl.of(dataSourceMock, PgHostImpl.ofUrl("jdbc:postgresql://secondary:6432"));
        final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory = pgConnection -> {
            if (pgConnection.equals(firstConnection)) {
                return firstStatisticsMock;
            }
            return secondStatisticsMock;
        };
        final DatabaseManagement management = new DatabaseManagementImpl(haPgConnectionMock, statisticsOnHostFactory);
        Mockito.when(haPgConnectionMock.getConnectionsToAllHostsInCluster())
            .thenReturn(Set.of(firstConnection, secondConnection));

        // False on all hosts
        Mockito.when(firstStatisticsMock.resetStatistics()).thenReturn(Boolean.FALSE);
        Mockito.when(secondStatisticsMock.resetStatistics()).thenReturn(Boolean.FALSE);
        assertThat(management.resetStatistics())
            .isFalse();
        Mockito.verify(firstStatisticsMock, Mockito.times(1)).resetStatistics();
        Mockito.verify(secondStatisticsMock, Mockito.times(1)).resetStatistics();
        Mockito.verifyNoMoreInteractions(firstStatisticsMock, secondStatisticsMock);

        // True on all hosts
        Mockito.when(firstStatisticsMock.resetStatistics()).thenReturn(Boolean.TRUE);
        Mockito.when(secondStatisticsMock.resetStatistics()).thenReturn(Boolean.TRUE);
        assertThat(management.resetStatistics())
            .isTrue();
        Mockito.verify(firstStatisticsMock, Mockito.times(2)).resetStatistics();
        Mockito.verify(secondStatisticsMock, Mockito.times(2)).resetStatistics();
        Mockito.verifyNoMoreInteractions(firstStatisticsMock, secondStatisticsMock);
    }
}
