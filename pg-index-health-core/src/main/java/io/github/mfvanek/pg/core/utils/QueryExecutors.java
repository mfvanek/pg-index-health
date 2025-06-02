/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.context.PgContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Utility class for executing SQL queries with various context parameters.
 */
public final class QueryExecutors {

    private static final Logger LOGGER = Logger.getLogger(QueryExecutors.class.getName());

    private QueryExecutors() {
        throw new UnsupportedOperationException();
    }

    /**
     * Executes a given SQL query and extracts results using the provided {@link ResultSetExtractor}.
     *
     * @param pgConnection the PostgreSQL connection, cannot be null
     * @param sqlQuery     the SQL query to execute, cannot be null
     * @param rse          the result set extractor to process the {@link ResultSet}, cannot be null
     * @param <T>          the type of the result
     * @return a list of results extracted by the {@link ResultSetExtractor}
     * @throws NullPointerException if any of the parameters are null
     * @throws PgSqlException       if a database access error occurs
     */
    public static <T> List<T> executeQuery(final PgConnection pgConnection,
                                           final String sqlQuery,
                                           final ResultSetExtractor<T> rse) {
        LOGGER.fine(() -> "Executing query: " + sqlQuery);
        Objects.requireNonNull(sqlQuery, "sqlQuery cannot be null");
        final DataSource dataSource = pgConnection.getDataSource();
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            final List<T> executionResult = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery(sqlQuery)) {
                while (resultSet.next()) {
                    executionResult.add(rse.extractData(resultSet));
                }
            }
            LOGGER.fine(() -> "Query completed with result " + executionResult);
            return executionResult;
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    /**
     * Executes a given SQL query within the context of the provided schema name.
     *
     * @param pgConnection the PostgreSQL connection, cannot be null
     * @param pgContext    the context containing the schema name, cannot be null
     * @param sqlQuery     the SQL query to execute, cannot be null
     * @param rse          the result set extractor to process the {@link ResultSet}, cannot be null
     * @param <T>          the type of the result
     * @return a list of results extracted by the {@link ResultSetExtractor}
     * @throws NullPointerException if any of the parameters are null
     * @throws PgSqlException       if a database access error occurs
     */
    public static <T> List<T> executeQueryWithSchema(final PgConnection pgConnection,
                                                     final PgContext pgContext,
                                                     final String sqlQuery,
                                                     final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
            } catch (SQLException e) {
                throw new PgSqlException(e);
            }
        });
    }

    /**
     * Executes a given SQL query within the context of the provided schema name and bloat threshold.
     *
     * @param pgConnection the PostgreSQL connection, cannot be null
     * @param pgContext    the context containing the schema name and bloat threshold, cannot be null
     * @param sqlQuery     the SQL query to execute, cannot be null
     * @param rse          the result set extractor to process the {@link ResultSet}, cannot be null
     * @param <T>          the type of the result
     * @return a list of results extracted by the {@link ResultSetExtractor}
     * @throws NullPointerException if any of the parameters are null
     * @throws PgSqlException       if a database access error occurs
     */
    public static <T> List<T> executeQueryWithBloatThreshold(final PgConnection pgConnection,
                                                             final PgContext pgContext,
                                                             final String sqlQuery,
                                                             final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
                statement.setDouble(2, pgContext.getBloatPercentageThreshold());
            } catch (SQLException e) {
                throw new PgSqlException(e);
            }
        });
    }

    /**
     * Executes a given SQL query within the context of the provided schema name and remaining percentage threshold.
     *
     * @param pgConnection the PostgreSQL connection, cannot be null
     * @param pgContext    the context containing the schema name and remaining percentage threshold, cannot be null
     * @param sqlQuery     the SQL query to execute, cannot be null
     * @param rse          the result set extractor to process the {@link ResultSet}, cannot be null
     * @param <T>          the type of the result
     * @return a list of results extracted by the {@link ResultSetExtractor}
     * @throws NullPointerException if any of the parameters are null
     * @throws PgSqlException       if a database access error occurs
     */
    public static <T> List<T> executeQueryWithRemainingPercentageThreshold(final PgConnection pgConnection,
                                                                           final PgContext pgContext,
                                                                           final String sqlQuery,
                                                                           final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
                statement.setDouble(2, pgContext.getRemainingPercentageThreshold());
            } catch (SQLException e) {
                throw new PgSqlException(e);
            }
        });
    }

    private static <T> List<T> executeQuery(final PgConnection pgConnection,
                                            final PgContext pgContext,
                                            final String sqlQuery,
                                            final ResultSetExtractor<T> rse,
                                            final Consumer<PreparedStatement> paramsSetter) {
        LOGGER.fine(() -> String.format(Locale.ROOT, "Executing query with context %s: %s", pgContext, sqlQuery));
        Objects.requireNonNull(sqlQuery, "sqlQuery");
        final DataSource dataSource = pgConnection.getDataSource();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            paramsSetter.accept(statement);
            final List<T> executionResult = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    executionResult.add(rse.extractData(resultSet));
                }
            }
            LOGGER.fine(() -> "Query completed with result " + executionResult);
            return executionResult;
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
