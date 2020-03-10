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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HighAvailabilityPgConnectionFactoryImplTest {

    private final HighAvailabilityPgConnectionFactory connectionFactory = new HighAvailabilityPgConnectionFactoryImpl(new PgConnectionFactoryImpl());

    @Test
    void onlyWriteUrl() {
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require", "postgres", "postgres");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(2));
    }

    @Test
    void writeAndReadUrl() {
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require", "postgres", "postgres",
                "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(4));
    }

    @Test
    void asyncReplica() {
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of("jdbc:postgresql://host-1:6432,host-2:6432/db_name?ssl=true&sslmode=require", "postgres", "postgres",
                "jdbc:postgresql://host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require",
                "jdbc:postgresql://host-5:6432/db_name?ssl=true&sslmode=require");
        assertNotNull(haPgConnection);
        assertThat(haPgConnection.getConnectionsToAllHostsInCluster(), hasSize(5));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        assertThrows(NullPointerException.class, () -> connectionFactory.of(null, null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> connectionFactory.of(null, null, null, "", null));
        assertThrows(NullPointerException.class, () -> connectionFactory.of(null, null, null, "jdbc:postgresql://host-1:6432/db_name", null));
        assertThrows(IllegalArgumentException.class, () -> connectionFactory.of(null, null, null, "jdbc:postgresql://host-1:6432/db_name", ""));
        assertThrows(NullPointerException.class, () -> connectionFactory.of(null, null, null, "jdbc:postgresql://host-1:6432/db_name", "jdbc:postgresql://host-2:6432/db_name"));
        assertThrows(IllegalArgumentException.class, () -> connectionFactory.of("", null, null, "jdbc:postgresql://host-1:6432/db_name", "jdbc:postgresql://host-2:6432/db_name"));
        assertThrows(NullPointerException.class, () -> connectionFactory.of("jdbc:postgresql://host-3:6432/db_name", null, null, "jdbc:postgresql://host-1:6432/db_name", "jdbc:postgresql://host-2:6432/db_name"));
        assertThrows(NullPointerException.class, () -> connectionFactory.of("jdbc:postgresql://host-3:6432/db_name", "postgres", null, "jdbc:postgresql://host-1:6432/db_name", "jdbc:postgresql://host-2:6432/db_name"));
    }
}
