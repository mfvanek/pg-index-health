/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.utils.PgSqlException;
import io.github.mfvanek.pg.utils.SharedDatabaseTestBase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;

class PrimaryHostDeterminerImplTest extends SharedDatabaseTestBase {

    private final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
    private final PgHost localhost = PgHostImpl.ofName("localhost");

    @Test
    void isPrimary() {
        final PgConnection pgConnection = PgConnectionImpl.of(getDataSource(), localhost);
        assertThat(primaryHostDeterminer.isPrimary(pgConnection)).isTrue();
    }

    @Test
    void isPrimaryWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        try (Connection connection = Mockito.mock(Connection.class);
             Statement statement = Mockito.mock(Statement.class)) {
            Mockito.when(dataSource.getConnection()).thenReturn(connection);
            Mockito.when(connection.createStatement()).thenReturn(statement);
            Mockito.when(statement.executeQuery(anyString())).thenThrow(new SQLException("bad query"));
            final PgConnection pgConnection = PgConnectionImpl.of(dataSource, localhost);
            assertThatThrownBy(() -> primaryHostDeterminer.isPrimary(pgConnection))
                    .isInstanceOf(PgSqlException.class)
                    .hasMessage("bad query")
                    .hasCauseInstanceOf(SQLException.class)
                    .hasRootCauseMessage("bad query");
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArgument() {
        assertThatThrownBy(() -> primaryHostDeterminer.isPrimary(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("pgConnection cannot be null");
    }

    @Test
    void isPrimaryForSecondaryHost() {
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=secondary", getPort());
        final PgConnection secondary = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl(readUrl));
        assertThat(secondary).isNotNull();
        assertThat(primaryHostDeterminer.isPrimary(secondary)).isFalse();
    }
}
