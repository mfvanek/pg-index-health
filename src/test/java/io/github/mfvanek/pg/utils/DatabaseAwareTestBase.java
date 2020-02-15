/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.PgContext;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;

public abstract class DatabaseAwareTestBase {

    protected static final long AMOUNT_OF_TRIES = 101L;

    private final DataSource dataSource;

    protected DatabaseAwareTestBase(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Nonnull
    private DatabasePopulator createDatabasePopulator() {
        return DatabasePopulator.builder(dataSource);
    }

    @Nonnull
    private DataSource getDataSource() {
        return dataSource;
    }

    protected void executeTestOnDatabase(@Nonnull final String schemaName,
                                         @Nonnull final DatabaseConfigurer databaseConfigurer,
                                         @Nonnull final TestExecutor testExecutor) {
        try (DatabasePopulator databasePopulator = createDatabasePopulator()) {
            databaseConfigurer.configure(databasePopulator)
                    .withSchema(schemaName)
                    .populate();
            testExecutor.execute(PgContext.of(schemaName, 0));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void waitForStatisticsCollector() {
        IntStream.of(1, 2, 3, 4).forEach((i) -> {
            try {
                // see PGSTAT_STAT_INTERVAL at https://github.com/postgres/postgres/blob/master/src/backend/postmaster/pgstat.c
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                fail(e);
            }
        });
    }

    protected long getSeqScansForAccounts(@Nonnull final PgContext pgContext) {
        final String sqlQuery =
                "select psat.relname::text as table_name, coalesce(psat.seq_scan, 0) as seq_scan\n" +
                        "from pg_catalog.pg_stat_all_tables psat\n" +
                        "where psat.schemaname = ?::text and psat.relname = 'accounts'::text;";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, pgContext.getSchemaName());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(2);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean existsStatisticsForTable(@Nonnull final PgContext pgContext, @Nonnull final String tableName) {
        final String sqlQuery =
                "select exists (select 1 from pg_catalog.pg_stats ps " +
                        "where ps.schemaname = ?::text and ps.tablename = ?::text);";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, pgContext.getSchemaName());
            statement.setString(2, Objects.requireNonNull(tableName));
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    protected String getPgVersion() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("select version()")) {
                resultSet.next();
                return resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean isDefaultSchema(@Nonnull final String schemaName) {
        return "public".equals(schemaName);
    }

    protected void tryToFindAccountByClientId(@Nonnull final String schemaName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (int counter = 0; counter < AMOUNT_OF_TRIES; ++counter) {
                statement.execute(String.format(
                        "select count(*) from %s.accounts where client_id = 1::bigint", schemaName));
            }
            DatabasePopulator.collectStatistics(dataSource, schemaName);
            waitForStatisticsCollector();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected long getRowsCount(@Nonnull final String schemaName,
                                @Nonnull final String tableName) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(
                    "select count(*) from " + schemaName + "." + tableName)) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
