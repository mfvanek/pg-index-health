/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.connection.PgHostImpl;

import javax.annotation.Nonnull;

public abstract class ClusterAwareTestBase {
    private static final PostgresSqlClusterWrapper POSTGRES_CLUSTER = new PostgresSqlClusterWrapper();

    @Nonnull
    protected PgConnection getFirstPgConnection() {
        return PgConnectionImpl.of(POSTGRES_CLUSTER.getDataSourceOne(), PgHostImpl.ofUrl(POSTGRES_CLUSTER.getFirstContainerJdbcUrl()));
    }

    @Nonnull
    protected PgConnection getSecondPgConnection() {
        return PgConnectionImpl.of(POSTGRES_CLUSTER.getDataSourceTwo(), PgHostImpl.ofUrl(POSTGRES_CLUSTER.getSecondContainerJdbcUrl()));
    }

    protected void stopFirstContainer() {
        POSTGRES_CLUSTER.stopFirstContainer();
    }

}
