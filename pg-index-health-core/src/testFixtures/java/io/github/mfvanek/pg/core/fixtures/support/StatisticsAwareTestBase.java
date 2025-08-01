/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import java.util.Locale;
import java.util.Objects;

public abstract class StatisticsAwareTestBase extends DatabaseAwareTestBase {

    protected static final long AMOUNT_OF_TRIES = 101L;

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

    private void waitForStatisticsCollector(@Nullable final String schemaName) {
        for (int i = 1; i <= 4; ++i) {
            sleep();
            if (schemaName != null && existsStatisticsForTable(schemaName, "clients") && existsStatisticsForTable(schemaName, "accounts")) {
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

    protected void collectStatistics(final String schemaName) {
        collectStatistics();
        waitForStatisticsCollector(schemaName);
    }

    protected void collectStatistics() {
        ExecuteUtils.executeOnDatabase(getDataSource(), statement -> statement.execute("vacuum analyze"));
        waitForStatisticsCollector(null);
    }
}
