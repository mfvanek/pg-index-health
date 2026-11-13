/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Base class for database tests that interact with and verify PostgreSQL statistics.
 */
public abstract class StatisticsAwareTestBase extends DatabaseAwareTestBase {

    /**
     * Default number of query executions to trigger statistics updates.
     */
    protected static final long AMOUNT_OF_TRIES = 101L;

    /**
     * Constructs a new instance of {@code StatisticsAwareTestBase}.
     */
    protected StatisticsAwareTestBase() {
    }

    /**
     * Retrieves the number of sequential scans recorded for the accounts table.
     *
     * @param pgContext the context of the PostgreSQL schema
     * @return the number of sequential scans
     */
    @SuppressWarnings("PMD.CheckResultSet")
    protected long getSeqScansForAccounts(final PgContext pgContext) {
        final String sqlQuery = """
            select psat.relname::text as table_name, coalesce(psat.seq_scan, 0) as seq_scan
            from pg_catalog.pg_stat_all_tables psat
            where psat.schemaname = ?::text and psat.relname = 'accounts'::text;""";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, pgContext.getSchemaName());
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(2);
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    /**
     * Checks if pg_stats statistics exist for the specified table.
     *
     * @param schemaName the schema name
     * @param tableName  the table name
     * @return {@code true} if statistics exist for the table, {@code false} otherwise
     */
    @SuppressWarnings("PMD.CheckResultSet")
    protected boolean existsStatisticsForTable(final String schemaName, final String tableName) {
        final String sqlQuery = """
            select exists (select 1 from pg_catalog.pg_stats ps
            where ps.schemaname = ?::text and ps.tablename = ?::text);""";
        try (Connection connection = getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setString(1, schemaName);
            statement.setString(2, Objects.requireNonNull(tableName));
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    /**
     * Executes queries on the accounts table repeatedly to generate statistics and runs vacuum analyze.
     *
     * @param schemaName the schema name
     */
    protected void tryToFindAccountByClientId(final String schemaName) {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            for (int counter = 0; counter < AMOUNT_OF_TRIES; ++counter) {
                statement.execute(String.format(Locale.ROOT, "select count(*) from %s.accounts where client_id = 1::bigint", schemaName));
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
        collectStatistics(schemaName);
    }

    private void waitForStatisticsCollector(@Nullable final String schemaName,
                                            final Collection<String> tableNames) {
        for (int i = 1; i <= 4; ++i) {
            sleep();
            if (schemaName != null && tableNames.stream().allMatch(t -> existsStatisticsForTable(schemaName, t))) {
                return;
            }
        }
    }

    private static void sleep() {
        try {
            // see PGSTAT_STAT_INTERVAL at https://github.com/postgres/postgres/blob/6b9501660c9384476ca9a04918f5cf94379e419e/src/backend/postmaster/pgstat.c#L78
            // see also https://github.com/postgres/postgres/blob/6cbed0ec791f3829d0e2092fd4c36d493ae75a50/src/backend/utils/activity/pgstat.c#L2
            Thread.sleep(500L); //NOSONAR
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Collects statistics and waits for the stats collector for the specified tables.
     *
     * @param schemaName the schema name
     * @param tableNames the names of tables to verify statistics for
     */
    protected void collectStatistics(final String schemaName, final Collection<String> tableNames) {
        collectStatistics();
        waitForStatisticsCollector(schemaName, tableNames);
    }

    /**
     * Collects statistics and waits for the stats collector for clients and accounts tables.
     *
     * @param schemaName the schema name
     */
    protected void collectStatistics(final String schemaName) {
        collectStatistics();
        waitForStatisticsCollector(schemaName, List.of("clients", "accounts"));
    }

    /**
     * Executes {@code vacuum analyze} on the database to collect statistics.
     */
    protected void collectStatistics() {
        ExecuteUtils.executeOnDatabase(getDataSource(), statement -> statement.execute("vacuum analyze"));
        waitForStatisticsCollector(null, List.of());
    }
}
