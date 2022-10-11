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

import java.time.Duration;
import javax.annotation.Nonnull;

/**
 * Provides access to HA postgresql cluster nodes.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
public final class PgConnectionAwareCluster implements AutoCloseable {

    // on some systems promoting to primary could take up to minute and even more
    public static final Duration MAX_WAIT_INTERVAL_SECONDS = PostgresSqlClusterWrapper.WAIT_INTERVAL_SECONDS.multipliedBy(2L);
    private final PostgresSqlClusterWrapper postgresCluster = new PostgresSqlClusterWrapper();

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        postgresCluster.close();
    }

    @Nonnull
    public PgConnection getFirstPgConnection() {
        return PgConnectionImpl.of(postgresCluster.getDataSourceForPrimary(), PgHostImpl.ofUrl(postgresCluster.getFirstContainerJdbcUrl()));
    }

    @Nonnull
    public PgConnection getSecondPgConnection() {
        return PgConnectionImpl.of(postgresCluster.getDataSourceForStandBy(), PgHostImpl.ofUrl(postgresCluster.getSecondContainerJdbcUrl()));
    }

    public void stopFirstContainer() {
        postgresCluster.stopFirstContainer();
    }
}
