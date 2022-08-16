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
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.support.SharedDatabaseTestBase;
import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

class QueryExecutorsTest extends SharedDatabaseTestBase {

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
            final PgConnection pgConnection = PgConnectionImpl.ofPrimary(dataSource);
            final PgContext context = PgContext.ofPublic();
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
            final PgConnection pgConnection = PgConnectionImpl.ofPrimary(dataSource);
            final PgContext context = PgContext.ofPublic();
            assertThatThrownBy(() -> QueryExecutors.executeQueryWithBloatThreshold(pgConnection, context, "select version()", rs -> rs.getString(1)))
                    .isInstanceOf(PgSqlException.class)
                    .hasMessage("bad parameter")
                    .hasCauseInstanceOf(SQLException.class)
                    .hasRootCauseMessage("bad parameter");
        }
    }
}
