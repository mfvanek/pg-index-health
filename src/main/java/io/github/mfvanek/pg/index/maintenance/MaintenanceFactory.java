/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;

/**
 * Factory for creating {@link IndexMaintenance} and {@link StatisticsMaintenance} objects with a given {@link PgConnection}.
 *
 * @author Ivan Vakhrushev
 * @see IndexMaintenance
 * @see StatisticsMaintenance
 * @see PgConnection
 */
public interface MaintenanceFactory {

    @Nonnull
    IndexMaintenance forIndex(@Nonnull PgConnection pgConnection);

    @Nonnull
    StatisticsMaintenance forStatistics(@Nonnull PgConnection pgConnection);
}
