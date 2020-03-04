/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.connection;

import io.github.mfvanek.pg.PostgresExtensionFactory;
import io.github.mfvanek.pg.PostgresDbExtension;
import io.github.mfvanek.pg.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PgConnectionHelperTest {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres =
            PostgresExtensionFactory.database();

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
        final int port = embeddedPostgres.getPort();
        return String.format(
                "jdbc:postgresql://localhost:%d/postgres?prepareThreshold=0&preparedStatementCacheQueries=0",
                port);
    }
}
