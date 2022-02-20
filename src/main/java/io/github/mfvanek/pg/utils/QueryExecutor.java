/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
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

public final class QueryExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);

    private QueryExecutor() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static <T> List<T> executeQuery(@Nonnull final PgConnection pgConnection,
                                           @Nonnull final String sqlQuery,
                                           @Nonnull final ResultSetExtractor<T> rse) {
        LOGGER.debug("Executing query: {}", sqlQuery);
        Objects.requireNonNull(sqlQuery, "sqlQuery");
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
            LOGGER.trace("Query failed", e);
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public static <T> List<T> executeQueryWithSchema(@Nonnull final PgConnection pgConnection,
                                                     @Nonnull final PgContext pgContext,
                                                     @Nonnull final String sqlQuery,
                                                     @Nonnull final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
            } catch (SQLException e) {
                LOGGER.trace("Error occurs while setting params", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Nonnull
    public static <T> List<T> executeQueryWithBloatThreshold(@Nonnull final PgConnection pgConnection,
                                                             @Nonnull final PgContext pgContext,
                                                             @Nonnull final String sqlQuery,
                                                             @Nonnull final ResultSetExtractor<T> rse) {
        return executeQuery(pgConnection, pgContext, sqlQuery, rse, statement -> {
            try {
                statement.setString(1, pgContext.getSchemaName());
                statement.setInt(2, pgContext.getBloatPercentageThreshold());
            } catch (SQLException e) {
                LOGGER.trace("Error occurs while setting params", e);
                throw new RuntimeException(e);
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
            LOGGER.trace("Query failed", e);
            throw new RuntimeException(e);
        }
    }
}
