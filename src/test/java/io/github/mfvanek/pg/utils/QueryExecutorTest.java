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
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.PgContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

public final class QueryExecutorTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final PgConnection pgConnection;

    QueryExecutorTest() {
        super(embeddedPostgres.getTestDatabase());
        this.pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
    }

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(QueryExecutor.class)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void executeInvalidQuery() {
        final String invalidSql = "select unknown_field from unknown_table";
        assertThatThrownBy(() -> QueryExecutor.executeQuery(pgConnection, invalidSql, (rs) -> null)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void executeInvalidQueryWithSchema() {
        final String invalidSqlWithParam = "select unknown_field from unknown_table where schema = ?::text";
        assertThatThrownBy(() -> QueryExecutor.executeQueryWithSchema(pgConnection, PgContext.of("s"), invalidSqlWithParam, (rs) -> null)).isInstanceOf(RuntimeException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void executeNullQuery() {
        assertThatThrownBy(() -> QueryExecutor.executeQuery(pgConnection, null, (rs) -> null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void executeQueryWithSchemaWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        final Connection connection = Mockito.mock(Connection.class);
        final PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
        Mockito.doAnswer(invocation -> {
            throw new SQLException("bad parameter");
        }).when(statement).setString(anyInt(), anyString());
        assertThatThrownBy(() -> QueryExecutor.executeQueryWithSchema(PgConnectionImpl.ofPrimary(dataSource), PgContext.ofPublic(), "select version()", (rs) -> rs.getString(1)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasMessageContaining("bad parameter");
    }

    @Test
    void executeQueryWithBloatThresholdWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        final Connection connection = Mockito.mock(Connection.class);
        final PreparedStatement statement = Mockito.mock(PreparedStatement.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(anyString())).thenReturn(statement);
        Mockito.doAnswer(invocation -> {
            throw new SQLException("bad parameter");
        }).when(statement).setString(anyInt(), anyString());
        assertThatThrownBy(() -> QueryExecutor.executeQueryWithBloatThreshold(PgConnectionImpl.ofPrimary(dataSource), PgContext.ofPublic(), "select version()", (rs) -> rs.getString(1)))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(SQLException.class)
                .hasMessageContaining("bad parameter");
    }
}
