/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class QueryExecutorsTest extends DatabaseAwareTestBase {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(QueryExecutors.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void executeInvalidQuery() {
        final String invalidSql = "select unknown_field from unknown_table";
        final PgConnection pgConnection = getPgConnection();
        assertThatThrownBy(() -> QueryExecutors.executeQuery(pgConnection, invalidSql, rs -> null))
            .isInstanceOf(PgSqlException.class)
            .hasCauseInstanceOf(SQLException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void executeInvalidQueryWithSchema() {
        final String invalidSqlWithParam = "select unknown_field from unknown_table where schema = ?::text";
        final PgContext context = PgContext.of("s");
        final PgConnection pgConnection = getPgConnection();
        assertThatThrownBy(() -> QueryExecutors.executeQueryWithSchema(pgConnection, context, invalidSqlWithParam, rs -> null))
            .isInstanceOf(PgSqlException.class)
            .hasCauseInstanceOf(SQLException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void executeNullQuery() {
        final PgConnection pgConnection = getPgConnection();
        assertThatThrownBy(() -> QueryExecutors.executeQuery(pgConnection, null, rs -> null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("sqlQuery cannot be null");
    }

    @Test
    void executeQueryWithSchemaWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        try (Connection connection = Mockito.mock(Connection.class);
             PreparedStatement statement = Mockito.mock(PreparedStatement.class)) {
            Mockito.when(dataSource.getConnection()).thenReturn(connection);
            Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
            Mockito.doAnswer(invocation -> {
                throw new SQLException("bad parameter");
            }).when(statement).setString(anyInt(), anyString());
            final PgConnection pgConnection = PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl("jdbc:postgresql://localhost:6432"));
            final PgContext context = PgContext.ofDefault();
            assertThatThrownBy(() -> QueryExecutors.executeQueryWithSchema(pgConnection, context, "select version()", rs -> rs.getString(1)))
                .isInstanceOf(PgSqlException.class)
                .hasMessage("bad parameter")
                .hasCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("bad parameter");
        }
    }

    @Test
    void executeQueryWithBloatThresholdWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        try (Connection connection = Mockito.mock(Connection.class);
             PreparedStatement statement = Mockito.mock(PreparedStatement.class)) {
            Mockito.when(dataSource.getConnection()).thenReturn(connection);
            Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
            Mockito.doAnswer(invocation -> {
                throw new SQLException("bad parameter");
            }).when(statement).setString(anyInt(), anyString());
            final PgConnection pgConnection = PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl("jdbc:postgresql://localhost:6432"));
            final PgContext context = PgContext.ofDefault();
            assertThatThrownBy(() -> QueryExecutors.executeQueryWithBloatThreshold(pgConnection, context, "select version()", rs -> rs.getString(1)))
                .isInstanceOf(PgSqlException.class)
                .hasMessage("bad parameter")
                .hasCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("bad parameter");
        }
    }

    @Test
    void executeQueryWithRemainingPercentageThreshold() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        try (Connection connection = Mockito.mock(Connection.class);
             PreparedStatement statement = Mockito.mock(PreparedStatement.class)) {
            Mockito.when(dataSource.getConnection()).thenReturn(connection);
            Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
            Mockito.doAnswer(invocation -> {
                throw new SQLException("bad parameter");
            }).when(statement).setString(anyInt(), anyString());
            Mockito.doAnswer(invocation -> {
                throw new SQLException("bad parameter");
            }).when(statement).setDouble(anyInt(), anyDouble());

            final PgConnection pgConnection = PgConnectionImpl.of(dataSource, PgHostImpl.ofUrl("jdbc:postgresql://localhost:6432"));
            final PgContext context = PgContext.ofDefault();
            final String sqlQuery = "SELECT version()";

            assertThatThrownBy(() -> QueryExecutors.executeQueryWithRemainingPercentageThreshold(pgConnection, context, sqlQuery, rs -> rs.getString(1)))
                .isInstanceOf(PgSqlException.class)
                .hasMessageContaining("bad parameter")
                .hasCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("bad parameter");
        }
    }
}
