/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.connection;

import com.opentable.db.postgres.embedded.ConnectionInfo;
import com.opentable.db.postgres.junit5.EmbeddedPostgresExtension;
import com.opentable.db.postgres.junit5.PreparedDbExtension;
import io.github.mfvanek.pg.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionHelperTest {

    @RegisterExtension
    static final PreparedDbExtension embeddedPostgres =
            EmbeddedPostgresExtension.preparedDatabase(ds -> {
            });

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(PgConnectionHelper.class));
    }

    @Test
    void createDataSource() {
        final DataSource dataSource = PgConnectionHelper.createDataSource(getWriteUrl(), "postgres", "postgres");
        assertNotNull(dataSource);
    }

    @Nonnull
    private String getWriteUrl() {
        final ConnectionInfo connectionInfo = embeddedPostgres.getConnectionInfo();
        return String.format(
                "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
                connectionInfo.getPort());
    }
}
