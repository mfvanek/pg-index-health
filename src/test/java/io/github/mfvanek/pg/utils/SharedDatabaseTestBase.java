/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionImpl;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import org.junit.jupiter.api.extension.RegisterExtension;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public abstract class SharedDatabaseTestBase extends DatabaseAwareTestBase {

    @RegisterExtension
    private static final PostgresDbExtension POSTGRES = PostgresExtensionFactory.database();

    protected SharedDatabaseTestBase() {
        super(POSTGRES.getTestDatabase());
    }

    @Nonnull
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.ofPrimary(POSTGRES.getTestDatabase());
    }

    @Nonnull
    protected static HighAvailabilityPgConnection getHaPgConnection() {
        return HighAvailabilityPgConnectionImpl.of(getPgConnection());
    }

    @Nonnull
    protected static ConnectionCredentials getConnectionCredentials() {
        return ConnectionCredentials.ofUrl(POSTGRES.getUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    @Nonnull
    protected static DataSource getDataSource() {
        return POSTGRES.getTestDatabase();
    }

    protected static int getPort() {
        return POSTGRES.getPort();
    }
}
