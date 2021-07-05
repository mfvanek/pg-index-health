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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionImplTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    @Test
    void getPrimaryDataSource() {
        final PgConnection connection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        assertNotNull(connection.getDataSource());
        assertThat(connection.getHost(), equalTo(PgHostImpl.ofPrimary()));
    }

    @Test
    void isPrimaryForAnyHost() {
        final int port = embeddedPostgres.getPort();
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=preferSecondary", port);
        final PgConnection any = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofUrl(readUrl));
        assertNotNull(any);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.ofPrimary(null));
        assertThrows(NullPointerException.class, () -> PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), null));
    }

    @Test
    void equalsAndHashCode() {
        final PgConnection first = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection theSame = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection second = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("second"));

        assertNotEquals(first, null);
        //noinspection AssertBetweenInconvertibleTypes
        assertNotEquals(first, BigDecimal.ZERO);

        // self
        assertEquals(first, first);
        assertEquals(first.hashCode(), first.hashCode());

        // the same
        assertEquals(first, theSame);
        assertEquals(first.hashCode(), theSame.hashCode());

        // others
        assertNotEquals(first, second);
        assertNotEquals(first.hashCode(), second.hashCode());

        // another implementation of PgConnection
        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        Mockito.when(connectionMock.getHost()).thenReturn(PgHostImpl.ofPrimary());
        assertEquals(first, connectionMock);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(PgConnectionImpl.class)
                .withIgnoredFields("dataSource")
                .verify();
    }

    @Test
    void toStringTest() {
        final PgConnection connection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        assertEquals("PgConnectionImpl{host=PgHostImpl{pgUrl='jdbc:postgresql://primary', hostNames=[primary], maybePrimary=true}}",
                connection.toString());
    }
}
