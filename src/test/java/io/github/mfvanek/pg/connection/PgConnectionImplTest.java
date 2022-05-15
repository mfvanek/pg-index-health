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
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PgConnectionImplTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    @Test
    void getPrimaryDataSource() {
        final PgConnection connection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        assertThat(connection.getDataSource()).isNotNull();
        assertThat(connection.getHost()).isEqualTo(PgHostImpl.ofPrimary());
    }

    @Test
    void isPrimaryForAnyHost() {
        final int port = embeddedPostgres.getPort();
        final String readUrl = String.format("jdbc:postgresql://localhost:%d/postgres?" +
                "prepareThreshold=0&preparedStatementCacheQueries=0&targetServerType=preferSecondary", port);
        final PgConnection any = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofUrl(readUrl));
        assertThat(any).isNotNull();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> PgConnectionImpl.ofPrimary(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), null)).isInstanceOf(NullPointerException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void equalsAndHashCode() {
        final PgConnection first = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection theSame = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        final PgConnection second = PgConnectionImpl.of(embeddedPostgres.getTestDatabase(), PgHostImpl.ofName("second"));

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(BigDecimal.ZERO)).isFalse();

        // self
        assertThat(first).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(first.hashCode());

        // the same
        assertThat(theSame).isEqualTo(first);
        assertThat(theSame.hashCode()).isEqualTo(first.hashCode());

        // others
        assertThat(second).isNotEqualTo(first);
        assertThat(second.hashCode()).isNotEqualTo(first.hashCode());

        // another implementation of PgConnection
        final PgConnection connectionMock = Mockito.mock(PgConnection.class);
        Mockito.when(connectionMock.getHost()).thenReturn(PgHostImpl.ofPrimary());
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
        final PgConnection connection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        assertThat(connection.toString()).isEqualTo("PgConnectionImpl{host=PgHostImpl{pgUrl='jdbc:postgresql://primary', hostNames=[primary], maybePrimary=true}}");
    }
}
