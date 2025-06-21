/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.support;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.testing.PostgreSqlContainerWrapper;
import org.jspecify.annotations.NonNull;

import javax.sql.DataSource;

public abstract class DatabaseAwareTestBase {

    private static final PostgreSqlContainerWrapper POSTGRES = PostgreSqlContainerWrapper.withDefaultVersion();

    @NonNull
    protected static PgConnection getPgConnection() {
        return PgConnectionImpl.of(getDataSource(), getHost());
    }

    @NonNull
    protected static PgHost getHost() {
        return PgHostImpl.ofUrl(POSTGRES.getUrl());
    }

    @NonNull
    protected static DataSource getDataSource() {
        return POSTGRES.getDataSource();
    }

    protected static int getPort() {
        return POSTGRES.getPort();
    }
}
