/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.extension.RegisterExtension;

class HighAvailabilityPgConnectionImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });
//
//    @Test
//    void getMasterDataSource() {
//        final var connectionInfo = embeddedPostgres.getConnectionInfo();
//        final var connection = HighAvailabilityPgConnectionImpl.of(getWriteUrl(), connectionInfo.getUser(), "postgres");
//        assertNotNull(connection.getDataSource());
//    }
//
//    @Test
//    void getReplicasDataSource() {
//        final var connectionInfo = embeddedPostgres.getConnectionInfo();
//        final var connection = HighAvailabilityPgConnectionImpl.of(
//                getWriteUrl(), connectionInfo.getUser(), "postgres", getReadUrl());
//        assertNotNull(connection.getReplicasDataSource());
//        assertThat(connection.getReplicasDataSource(), hasSize(1));
//    }
//
//    @Test
//    void withCascadeAsyncReplica() {
//        final var connectionInfo = embeddedPostgres.getConnectionInfo();
//        final var connection = HighAvailabilityPgConnectionImpl.of(
//                getWriteUrl(), connectionInfo.getUser(), "postgres", getReadUrl(), getReadUrl());
//        assertNotNull(connection.getReplicasDataSource());
//        assertThat(connection.getReplicasDataSource(), hasSize(1));
//    }
//
//    @Test
//    void withInvalidArguments() {
//        assertThrows(NullPointerException.class, () -> HighAvailabilityPgConnectionImpl.of(null, null, null));
//        assertThrows(IllegalArgumentException.class, () -> HighAvailabilityPgConnectionImpl.of("", null, null));
//        assertThrows(NullPointerException.class, () -> HighAvailabilityPgConnectionImpl.of("jdbc:postgresql://localhost", null, null));
//        assertThrows(IllegalArgumentException.class, () -> HighAvailabilityPgConnectionImpl.of("jdbc:postgresql://localhost", "", null));
//        assertThrows(NullPointerException.class, () -> HighAvailabilityPgConnectionImpl.of("jdbc:postgresql://localhost", "postgres", null));
//        assertThrows(IllegalArgumentException.class, () -> HighAvailabilityPgConnectionImpl.of("jdbc:postgresql://localhost", "postgres", ""));
//    }
//
//    @Nonnull
//    private String getWriteUrl() {
//        final var connectionInfo = embeddedPostgres.getConnectionInfo();
//        return String.format(
//                "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
//                connectionInfo.getPort());
//    }
//
//    private String getReadUrl() {
//        final var connectionInfo = embeddedPostgres.getConnectionInfo();
//        return String.format(
//                "jdbc:postgresql://localhost:%d/postgres?readOnly=true&prepareThreshold=0&preparedStatementCacheQueries=0",
//                connectionInfo.getPort());
//    }
}
