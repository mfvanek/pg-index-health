/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

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
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require", "postgres", "postgres");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(2));
        checkPrimary(haPgConnection);
    }

    @Test
    void writeAndReadUrl() {
        Mockito.doAnswer(invocation -> {
            final PgConnection connection = invocation.getArgument(0, PgConnection.class);
            return "host-2".equals(connection.getHost().getName());
        }).when(primaryHostDeterminer).isPrimary(any(PgConnection.class));
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrls(
                Arrays.asList("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require",
                        "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require"),
                "postgres", "postgres");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(4));
        checkPrimary(haPgConnection);
    }

    @Test
    void asyncReplica() {
        Mockito.doAnswer(invocation -> {
            final PgConnection connection = invocation.getArgument(0, PgConnection.class);
            return "host-2".equals(connection.getHost().getName());
        }).when(primaryHostDeterminer).isPrimary(any(PgConnection.class));
        final ConnectionCredentials credentials = ConnectionCredentials.of(Arrays.asList("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require",
                "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require",
                "jdbc:postgresql://host-5:6432/db_name?ssl=true&sslmode=require"), "postgres", "postgres");
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of(credentials);
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(5));
        checkPrimary(haPgConnection);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> connectionFactory.of(null));

        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrl(null, null, null));
        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432/db_name", null, null));
        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432/db_name", "u", null));

        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrls(null, null, null));
        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrls(Collections.singleton("jdbc:postgresql://host-1:6432/db_name"), null, null));
        assertThrows(NullPointerException.class, () -> connectionFactory.ofUrls(Collections.singleton("jdbc:postgresql://host-1:6432/db_name"), "u", null));
    }

    @Test
    void shouldFailWhenPrimaryHosNotFound() {
        Mockito.when(primaryHostDeterminer.isPrimary(any(PgConnection.class))).thenReturn(false);
        final NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> connectionFactory.ofUrl("jdbc:postgresql://host-1:6432,host-2:6432/db_name", "postgres", "postgres"));
        assertThat(exception.getMessage(), startsWith("Connection to primary host not found in "));
    }

    @Test
    void shouldNotFailWhenSplitBrainOrMultyMasterConfiguration() {
        Mockito.when(primaryHostDeterminer.isPrimary(any(PgConnection.class))).thenReturn(true);
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.ofUrl("jdbc:postgresql://host-D:6432,host-A:6432/db_name", "postgres", "postgres");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(2));
        assertNotNull(haPgConnection.getConnectionToPrimary());
        assertThat(haPgConnection.getConnectionToPrimary().getHost().getName(), equalTo("host-A"));
    }

    private void checkPrimary(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        assertNotNull(haPgConnection.getConnectionToPrimary());
        assertTrue(primaryHostDeterminer.isPrimary(haPgConnection.getConnectionToPrimary()));
        for (PgConnection connection : haPgConnection.getConnectionsToAllHostsInCluster()) {
            if (primaryHostDeterminer.isPrimary(connection)) {
                assertSame(haPgConnection.getConnectionToPrimary(), connection);
            }
        }
    }
}
