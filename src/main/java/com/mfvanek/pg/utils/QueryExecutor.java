/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import com.mfvanek.pg.connection.PgConnection;
import com.mfvanek.pg.model.PgContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class QueryExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryExecutor.class);

    private QueryExecutor() {
        throw new UnsupportedOperationException();
    }

    public static <T> List<T> executeQuery(@Nonnull final PgConnection pgConnection,
                                           @Nonnull final String sqlQuery,
                                           @Nonnull final ResultSetExtractor<T> rse) {
        LOGGER.debug("Executing query: {}", sqlQuery);
        try (Connection connection = pgConnection.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            final List<T> executionResult = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery(Objects.requireNonNull(sqlQuery))) {
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

    public static <T> List<T> executeQuery(@Nonnull final PgConnection pgConnection,
                                           @Nonnull final PgContext pgContext,
                                           @Nonnull final String sqlQuery,
                                           @Nonnull final ResultSetExtractor<T> rse) {
        LOGGER.debug("Executing query: {}", sqlQuery);
        try (Connection connection = pgConnection.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(Objects.requireNonNull(sqlQuery))) {
            statement.setString(1, pgContext.getSchemaName());
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
