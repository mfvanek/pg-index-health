/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.statistics;

import io.github.mfvanek.pg.connection.PgConnection;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@Tag("fast")
class StatisticsMaintenanceOnHostImplUnitTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldFailOnNullArguments() {
        assertThatThrownBy(() -> new StatisticsMaintenanceOnHostImpl(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("pgConnection cannot be null");

        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        assertThatThrownBy(() -> new StatisticsMaintenanceOnHostImpl(connectionMock, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("queryExecutor cannot be null");
    }

    @Test
    void resetStatisticsShouldReturnFalseOnEmptyResultSet() {
        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        final StatisticsQueryExecutor executorMock = Mockito.mock(StatisticsQueryExecutor.class);
        final StatisticsMaintenanceOnHost maintenance = new StatisticsMaintenanceOnHostImpl(connectionMock, executorMock);
        Mockito.when(executorMock.executeQuery(any(), any(), any()))
            .thenReturn(List.of());
        assertThat(maintenance.resetStatistics())
            .isFalse();
    }

    @Test
    void resetStatisticsShouldReturnFalseWhenFirstRowIsFalse() {
        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        final StatisticsQueryExecutor executorMock = Mockito.mock(StatisticsQueryExecutor.class);
        final StatisticsMaintenanceOnHost maintenance = new StatisticsMaintenanceOnHostImpl(connectionMock, executorMock);
        Mockito.when(executorMock.executeQuery(any(), any(), any()))
            .thenReturn(List.of(Boolean.FALSE, Boolean.TRUE));
        assertThat(maintenance.resetStatistics())
            .isFalse();
    }
}
