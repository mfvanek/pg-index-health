/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.factory;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PrimaryHostDeterminer;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Tag("fast")
class HighAvailabilityPgConnectionFactoryImplTest {

    private final PrimaryHostDeterminer primaryHostDeterminer = Mockito.mock(PrimaryHostDeterminer.class);
    private final HighAvailabilityPgConnectionFactory connectionFactory =
        new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl(), primaryHostDeterminer);

    @Test
    void onlyWriteUrl() {
        Mockito.doAnswer(invocation -> {
            final PgConnection connection = invocation.getArgument(0, PgConnection.class);
            return "host-1".equals(connection.getHost().getName());
        }).when(primaryHostDeterminer).isPrimary(any(PgConnection.class));
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrl(
            "jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require", "postgres", "postgres");
        assertThat(haPgConnection)
            .isNotNull()
            .satisfies(c -> assertThat(c.getConnectionsToAllHostsInCluster())
                .hasSize(2)
                .isUnmodifiable());
        checkPrimary(haPgConnection);
    }

    @Test
    void writeAndReadUrl() {
        Mockito.doAnswer(invocation -> {
            final PgConnection connection = invocation.getArgument(0, PgConnection.class);
            return "host-2".equals(connection.getHost().getName());
        }).when(primaryHostDeterminer).isPrimary(any(PgConnection.class));
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrls(
            List.of("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require",
                "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require"),
            "postgres", "postgres");
        assertThat(haPgConnection)
            .isNotNull()
            .satisfies(c -> assertThat(c.getConnectionsToAllHostsInCluster())
                .hasSize(4)
                .isUnmodifiable());
        checkPrimary(haPgConnection);
    }

    @Test
    void asyncReplica() {
        Mockito.doAnswer(invocation -> {
            final PgConnection connection = invocation.getArgument(0, PgConnection.class);
            return "host-2".equals(connection.getHost().getName());
        }).when(primaryHostDeterminer).isPrimary(any(PgConnection.class));
        final ConnectionCredentials credentials = ConnectionCredentials.of(List.of(
            "jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require",
            "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require",
            "jdbc:postgresql://host-5:6432/db_name?ssl=true&sslmode=require"), "postgres", "postgres");
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of(credentials);
        assertThat(haPgConnection)
            .isNotNull()
            .satisfies(c -> assertThat(c.getConnectionsToAllHostsInCluster())
                .hasSize(5)
                .isUnmodifiable());
        checkPrimary(haPgConnection);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThatThrownBy(() -> connectionFactory.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("credentials cannot be null");

        assertThatThrownBy(() -> connectionFactory.ofUrl(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("writeUrl cannot be null");
        assertThatThrownBy(() -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432/db_name", null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userName cannot be null");
        assertThatThrownBy(() -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432/db_name", "u", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("password cannot be null");

        assertThatThrownBy(() -> connectionFactory.ofUrls(null, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("connectionUrls cannot be null");
        final Set<String> urls = Set.of("jdbc:postgresql://host-1:6432/db_name");
        assertThatThrownBy(() -> connectionFactory.ofUrls(urls, null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("userName cannot be null");
        assertThatThrownBy(() -> connectionFactory.ofUrls(urls, "u", null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("password cannot be null");
    }

    @Test
    void shouldFailWhenPrimaryHosNotFound() {
        Mockito.when(primaryHostDeterminer.isPrimary(any(PgConnection.class))).thenReturn(Boolean.FALSE);
        assertThatThrownBy(() -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432/db_name", "postgres", "postgres"))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessageStartingWith("Connection to primary host not found in ");
    }

    @Test
    void shouldNotFailWhenSplitBrainOrMultyMasterConfiguration() {
        Mockito.when(primaryHostDeterminer.isPrimary(any(PgConnection.class))).thenReturn(Boolean.TRUE);
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrl(
            "jdbc:postgresql://host-D:6432,host-A:6432/db_name", "postgres", "postgres");
        assertThat(haPgConnection)
            .isNotNull()
            .satisfies(c -> assertThat(c.getConnectionsToAllHostsInCluster())
                .hasSize(2)
                .isUnmodifiable());
        assertThat(haPgConnection.getConnectionToPrimary())
            .isNotNull()
            .satisfies(c -> assertThat(c.getHost().getName()).isEqualTo("host-A"));
    }

    @SuppressWarnings("DirectInvocationOnMock")
    private void checkPrimary(@NonNull final HighAvailabilityPgConnection haPgConnection) {
        assertThat(haPgConnection.getConnectionToPrimary())
            .isNotNull()
            .satisfies(c -> verify(primaryHostDeterminer).isPrimary(c));
        for (final PgConnection connection : haPgConnection.getConnectionsToAllHostsInCluster()) {
            if (primaryHostDeterminer.isPrimary(connection)) {
                assertThat(connection).isSameAs(haPgConnection.getConnectionToPrimary());
            }
        }
    }
}
