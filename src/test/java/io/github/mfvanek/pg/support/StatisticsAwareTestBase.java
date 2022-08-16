/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.utils.PgSqlException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.annotation.Nonnull;

public abstract class StatisticsAwareTestBase extends DatabaseAwareTestBase {

    protected static final long AMOUNT_OF_TRIES = 101L;

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
            throw new PgSqlException(e);
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
            throw new PgSqlException(e);
        }
    }

    protected void tryToFindAccountByClientId(@Nonnull final String schemaName) {
        try (Connection connection = getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            for (int counter = 0; counter < AMOUNT_OF_TRIES; ++counter) {
                statement.execute(String.format(
                        "select count(*) from %s.accounts where client_id = 1::bigint", schemaName));
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
        DatabasePopulator.collectStatistics(getDataSource(), schemaName);
        TestUtils.waitForStatisticsCollector();
    }
}
