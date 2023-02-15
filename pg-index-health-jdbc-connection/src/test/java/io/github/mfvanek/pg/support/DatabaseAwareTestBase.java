/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.testing.PostgreSqlContainerWrapper;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = PostgreSqlContainerWrapper.withDefaultVersion();

    @Nonnull
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.ofPrimary(getDataSource());
    }

    @Nonnull
    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    protected static int getPort() {
        return POSTGRES.getPort();
    }
}
