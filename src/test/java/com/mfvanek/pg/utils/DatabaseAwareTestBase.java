/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class DatabaseAwareTestBase {

    private final DataSource dataSource;

    protected DatabaseAwareTestBase(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Nonnull
    protected DatabasePopulator createDatabasePopulator() {
        return new DatabasePopulator(dataSource);
    }

    @Nonnull
    protected DataSource getDataSource() {
        return dataSource;
    }

    protected void executeTestOnDatabase(@Nonnull final Consumer<DatabasePopulator> databasePopulatorConsumer,
                                         @Nonnull final TestExecutor testExecutor) {
        try (var databasePopulator = createDatabasePopulator()) {
            databasePopulatorConsumer.accept(databasePopulator);
            testExecutor.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void waitForStatisticsCollector() {
        IntStream.of(1, 2, 3).forEach((i) -> {
            try {
                // see PGSTAT_STAT_INTERVAL at https://github.com/postgres/postgres/blob/master/src/backend/postmaster/pgstat.c
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                fail(e);
            }
        });
    }

    protected long getSeqScansForAccounts() {
        final String sqlQuery =
                "select psat.relname::text as table_name, coalesce(psat.seq_scan, 0) as seq_scan\n" +
                        "from pg_catalog.pg_stat_all_tables psat\n" +
                        "where psat.schemaname = 'public'::text and psat.relname = 'accounts'::text;";
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                resultSet.next();
                return resultSet.getLong(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
