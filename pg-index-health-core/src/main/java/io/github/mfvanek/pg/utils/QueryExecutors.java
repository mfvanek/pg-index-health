/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.exception.PgSqlException;
import io.github.mfvanek.pg.model.context.PgContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * Utility class for executing SQL queries with various context parameters.
 */
public final class QueryExecutors {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutors.class);

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
    @Nonnull
    public static <T> List<T> executeQuery(@Nonnull final PgConnection pgConnection,
                                           @Nonnull final String sqlQuery,
                                           @Nonnull final ResultSetExtractor<T> rse) {
        LOGGER.debug("Executing query: {}", sqlQuery);
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
            LOGGER.debug("Query completed with result {}", executionResult);
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
    @Nonnull
    public static <T> List<T> executeQueryWithSchema(@Nonnull final PgConnection pgConnection,
                                                     @Nonnull final PgContext pgContext,
                                                     @Nonnull final String sqlQuery,
                                                     @Nonnull final ResultSetExtractor<T> rse) {
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
    @Nonnull
    public static <T> List<T> executeQueryWithBloatThreshold(@Nonnull final PgConnection pgConnection,
                                                             @Nonnull final PgContext pgContext,
                                                             @Nonnull final String sqlQuery,
                                                             @Nonnull final ResultSetExtractor<T> rse) {
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
    @Nonnull
    public static <T> List<T> executeQueryWithRemainingPercentageThreshold(@Nonnull final PgConnection pgConnection,
                                                                           @Nonnull final PgContext pgContext,
                                                                           @Nonnull final String sqlQuery,
                                                                           @Nonnull final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
                statement.setDouble(2, pgContext.getRemainingPercentageThreshold());
            } catch (SQLException e) {
                throw new PgSqlException(e);
            }
        });
    }

    @Nonnull
    private static <T> List<T> executeQuery(@Nonnull final PgConnection pgConnection,
                                            @Nonnull final PgContext pgContext,
                                            @Nonnull final String sqlQuery,
                                            @Nonnull final ResultSetExtractor<T> rse,
                                            @Nonnull final Consumer<PreparedStatement> paramsSetter) {
        LOGGER.debug("Executing query with context {}: {}", pgContext, sqlQuery);
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
            LOGGER.debug("Query completed with result {}", executionResult);
            return executionResult;
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
