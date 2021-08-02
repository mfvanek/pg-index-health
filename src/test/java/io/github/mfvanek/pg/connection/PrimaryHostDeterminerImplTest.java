/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        assertTrue(primaryHostDeterminer.isPrimary(pgConnection));
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
        final RuntimeException runtimeException = assertThrows(RuntimeException.class,
                () -> primaryHostDeterminer.isPrimary(pgConnection));
        final Throwable cause = runtimeException.getCause();
        assertNotNull(cause);
        assertThat(cause, instanceOf(SQLException.class));
        assertEquals("bad query", cause.getMessage());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArgument() {
        assertThrows(NullPointerException.class, () -> primaryHostDeterminer.isPrimary(null));
    }

    @Test
    void isPrimaryForSecondaryHost() {
        final int port = embeddedPostgres.getPort();
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=secondary", port);
        final PgConnection secondary = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofUrl(readUrl));
        assertNotNull(secondary);
        assertFalse(primaryHostDeterminer.isPrimary(secondary));
    }
}
