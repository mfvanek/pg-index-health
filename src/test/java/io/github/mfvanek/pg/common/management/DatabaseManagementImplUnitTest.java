/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class DatabaseManagementImplUnitTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionWhenInvalidArgumentsArePassed() {
        assertThatThrownBy(() -> new DatabaseManagementImpl(null, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("haPgConnection cannot be null");

        final HighAvailabilityPgConnection haPgConnection = Mockito.mock(HighAvailabilityPgConnection.class);
        assertThatThrownBy(() -> new DatabaseManagementImpl(haPgConnection, null, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("statisticsOnHostFactory cannot be null");

        final Function<PgConnection, StatisticsMaintenanceOnHost> statisticsOnHostFactory = pgConnection -> Mockito.mock(StatisticsMaintenanceOnHost.class);
        assertThatThrownBy(() -> new DatabaseManagementImpl(haPgConnection, statisticsOnHostFactory, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("configurationOnHostFactory cannot be null");
    }
}
