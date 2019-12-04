/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PgConnectionHelperTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    @Test
    void createDataSource() {
        final var dataSource = PgConnectionHelper.createDataSource(getWriteUrl(), "postgres", "postgres");
        assertNotNull(dataSource);
    }

    @Nonnull
    private String getWriteUrl() {
        final var connectionInfo = embeddedPostgres.getConnectionInfo();
        return String.format(
                "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
                connectionInfo.getPort());
    }
}
