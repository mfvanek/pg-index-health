/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=preferSecondary", port);
        final PgConnection any = PgConnectionImpl.of(getDataSource(), PgHostImpl.ofUrl(readUrl));
        assertThat(any).isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        final DataSource dataSource = getDataSource();
        assertThatThrownBy(() -> PgConnectionImpl.of(dataSource, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("host cannot be null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgHost host = PgHostImpl.ofUrl("jdbc:postgresql://first:6432");
        final PgConnection first = PgConnectionImpl.of(getDataSource(), host);
        final PgConnection theSame = PgConnectionImpl.of(getDataSource(), host);
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
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
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
}
