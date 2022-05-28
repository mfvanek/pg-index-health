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

import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;

class PrimaryHostDeterminerImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
    private final PgHost localhost = PgHostImpl.ofName("localhost");

    PrimaryHostDeterminerImplTest() {
        super(embeddedPostgres.getTestDatabase());
    }

    @Test
    void isPrimary() {
        final PgConnection pgConnection = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), localhost);
        assertThat(primaryHostDeterminer.isPrimary(pgConnection)).isTrue();
    }

    @Test
    void isPrimaryWithExecutionError() throws SQLException {
        final DataSource dataSource = Mockito.mock(DataSource.class);
        final Connection connection = Mockito.mock(Connection.class);
        final Statement statement = Mockito.mock(Statement.class);
        Mockito.when(dataSource.getConnection()).thenReturn(connection);
        Mockito.when(connection.createStatement()).thenReturn(statement);
        Mockito.when(statement.executeQuery(anyString())).thenThrow(new SQLException("bad query"));
        final PgConnection pgConnection = PgConnectionImpl.of(dataSource, localhost);
        assertThatThrownBy(() -> primaryHostDeterminer.isPrimary(pgConnection))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Query failed")
                .hasCauseInstanceOf(SQLException.class)
                .hasRootCauseMessage("bad query");
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
        final int port = embeddedPostgres.getPort();
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=secondary", port);
        final PgConnection secondary = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofUrl(readUrl));
        assertThat(secondary).isNotNull();
        assertThat(primaryHostDeterminer.isPrimary(secondary)).isFalse();
    }
}
