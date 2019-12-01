/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.sql.DataSource;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimplePgConnectionTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    @Test
    void getMasterDataSource() {
        final var connection = SimplePgConnection.of(embeddedPostgres.getTestDatabase());
        assertNotNull(connection.getMasterDataSource());

        assertThrows(NullPointerException.class, () -> SimplePgConnection.of(null));
    }

    @Test
    void getReplicasDataSource() {
        final var dataSource = embeddedPostgres.getTestDatabase();
        final var connection = SimplePgConnection.of(dataSource, dataSource);
        assertNotNull(connection.getReplicasDataSource());
        assertThat(connection.getReplicasDataSource(), hasSize(1));

        DataSource replica = null;
        assertThrows(NullPointerException.class, () -> SimplePgConnection.of(dataSource, replica));

        List<DataSource> replicas = null;
        assertThrows(NullPointerException.class, () -> SimplePgConnection.of(dataSource, replicas));
    }

    @Test
    void getReplicasCount() {
        var connection = SimplePgConnection.of(embeddedPostgres.getTestDatabase());
        assertEquals(0, connection.getReplicasCount());

        connection = SimplePgConnection.of(embeddedPostgres.getTestDatabase(), embeddedPostgres.getTestDatabase());
        assertEquals(1, connection.getReplicasCount());
    }
}
