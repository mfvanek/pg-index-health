/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.settings.ImportantParam;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = new PostgreSqlContainerWrapper(List.of(
            Map.entry(ImportantParam.LOCK_TIMEOUT.getName(), "1000"),
            Map.entry(ImportantParam.SHARED_BUFFERS.getName(), "256MB"),
            Map.entry(ImportantParam.MAINTENANCE_WORK_MEM.getName(), "128MB"),
            Map.entry(ImportantParam.WORK_MEM.getName(), "16MB"),
            Map.entry(ImportantParam.RANDOM_PAGE_COST.getName(), "1")
    ));

    @Nonnull
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.ofPrimary(getDataSource());
    }

    @Nonnull
    protected static HighAvailabilityPgConnection getHaPgConnection() {
        return HighAvailabilityPgConnectionImpl.of(getPgConnection());
    }

    @Nonnull
    protected static ConnectionCredentials getConnectionCredentials() {
        return ConnectionCredentials.ofUrl(POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Nonnull
    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    protected static int getPort() {
        return POSTGRES.getPort();
    }

    protected void executeTestOnDatabase(@Nonnull final String schemaName,
                                         @Nonnull final DatabaseConfigurer databaseConfigurer,
                                         @Nonnull final Consumer<PgContext> testExecutor) {
        try (DatabasePopulator databasePopulator = DatabasePopulator.builder(getDataSource(), schemaName, isProceduresSupported())) {
            databaseConfigurer.configure(databasePopulator)
                    .populate();
            testExecutor.accept(PgContext.of(schemaName, 0));
        }
    }

    protected static boolean isCumulativeStatisticsSystemSupported() {
        return POSTGRES.isCumulativeStatisticsSystemSupported();
    }

    protected static boolean isProceduresSupported() {
        return POSTGRES.isProceduresSupported();
    }

    protected static boolean isProceduresNotSupported() {
        return !isProceduresSupported();
    }

    protected static boolean isOutParametersInProcedureSupported() {
        return POSTGRES.isOutParametersInProcedureSupported();
    }
}
