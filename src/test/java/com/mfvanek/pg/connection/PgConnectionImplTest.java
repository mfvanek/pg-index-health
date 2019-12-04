/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionImplTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    @Test
    void getMasterDataSource() {
        final var connection = PgConnectionImpl.ofMaster(embeddedPostgres.getTestDatabase());
        assertNotNull(connection.getDataSource());
        assertThat(connection.getHost(), equalTo(PgHostImpl.ofMaster()));

        assertThrows(NullPointerException.class, () -> PgConnectionImpl.ofMaster(null));
    }

    @Test
    void getReplicasDataSource() {
        final var dataSource = embeddedPostgres.getTestDatabase();
        final var connection = PgConnectionImpl.ofReplica(dataSource);
        assertNotNull(connection.getDataSource());
        assertThat(connection.getHost(), equalTo(PgHostImpl.ofReplica()));

        assertThrows(NullPointerException.class, () -> PgConnectionImpl.ofReplica(null));
    }
}
