/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.connection.support.DatabaseAwareTestBase;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgConnectionImplTest extends DatabaseAwareTestBase {

    @Test
    void getPrimaryDataSource() {
        final PgConnection connection = getPgConnection();
        assertThat(connection.getDataSource())
            .isNotNull();
        assertThat(connection.getHost())
            .isEqualTo(getHost());
    }

    @Test
    void isPrimaryForAnyHost() {
        final int port = getPort();
        final String readUrl = String.format(Locale.ROOT, """
            jdbc:postgresql://localhost:%d/postgres?\
            prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=preferSecondary""", port);
        final PgConnection any = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl(readUrl));
        assertThat(any).isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> PgConnectionImpl.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("dataSource cannot be null");

        final DataSource dataSource = getDataSource();
        assertThatThrownBy(() -> PgConnectionImpl.of(dataSource, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("host cannot be null");

        assertThatThrownBy(() -> PgConnectionImpl.ofUrl(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("dataSource cannot be null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://first:6432");
        final PgConnection first = PgConnectionImpl.of(getDataSource(), host);
        final PgConnection theSame = PgConnectionImpl.ofUrl(getDataSource(), "jdbc:postgresql://first:6432");
        final PgConnection second = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl("jdbc:postgresql://second:5432"));

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        // another implementation of PgConnection
        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        Mockito.when(connectionMock.getHost()).thenReturn(PgHostImpl.ofUrl("jdbc:postgresql://first:6432"));
        assertThat(first).isEqualTo(connectionMock);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgConnectionImpl.class)
            .withIgnoredFields("dataSource")
            .verify();
    }

    @Test
    void toStringTest() {
        final PgConnection connection = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl("jdbc:postgresql://primary:5432"));
        assertThat(connection)
            .hasToString("PgConnectionImpl{host=PgHostImpl{pgUrl='jdbc:postgresql://primary:5432', hostName=primary, port=5432, maybePrimary=true}}");
    }

    @Test
    void twoConnectionsDifferentSameHostWithDifferentPortsConsideredNotEqual() {
        final DataSource dataSourceMock = Mockito.mock(DataSource.class);
        final PgConnection firstPgConnection = PgConnectionImpl.of(dataSourceMock, PgHostImpl.ofUrl("jdbc:postgresql://localhost:5432"));
        final PgConnection secondPgConnection = PgConnectionImpl.of(dataSourceMock, PgHostImpl.ofUrl("jdbc:postgresql://localhost:5431"));

        assertThat(firstPgConnection).isNotEqualTo(secondPgConnection);
    }

    @Test
    void shouldGetUrlFromConnectionMetadata() {
        final PgConnection first = PgConnectionImpl.ofUrl(getDataSource(), null);
        assertThat(first.getHost())
            .isNotNull()
            .isEqualTo(getHost());

        final PgConnection second = PgConnectionImpl.ofUrl(getDataSource(), "   ");
        assertThat(second.getHost())
            .isNotNull()
            .isEqualTo(getHost());

        final PgConnection third = PgConnectionImpl.ofUrl(getDataSource(), "jdbc:tc:postgresql:17.4:///demo");
        assertThat(third.getHost())
            .isNotNull()
            .isEqualTo(getHost());
    }

    @Test
    void withExceptionWhileObtainingUrlFromMetadata() throws SQLException {
        final DataSource dataSourceMock = Mockito.mock(DataSource.class);
        try (Connection connectionMock = Mockito.mock(Connection.class)) {
            Mockito.when(dataSourceMock.getConnection())
                .thenReturn(connectionMock);
            Mockito.when(connectionMock.getMetaData())
                .thenThrow(new SQLException("Unable to obtain connection from metadata"));

            assertThatThrownBy(() -> PgConnectionImpl.ofUrl(dataSourceMock, null))
                .isInstanceOf(PgSqlException.class)
                .hasMessage("Unable to obtain connection from metadata")
                .hasCauseInstanceOf(SQLException.class);
        }
    }
}
