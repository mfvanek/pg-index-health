/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.e2e;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.connection.host.PgHostImpl;
import io.github.mfvanek.pg.testing.PostgreSqlClusterWrapper;

import java.time.Duration;

/**
 * Provides access to HA postgresql cluster nodes.
 *
 * @author Alexey Antipin
 * @since 0.6.2
 */
final class PgConnectionAwareCluster implements AutoCloseable {

    // on some systems promoting to primary could take up to minute and even more
    public static final Duration MAX_WAIT_INTERVAL_SECONDS = PostgreSqlClusterWrapper.WAIT_INTERVAL_SECONDS.multipliedBy(2L);
    private final PostgreSqlClusterWrapper postgresCluster = PostgreSqlClusterWrapper.builder().build();

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        postgresCluster.close();
    }

    public PgConnection getFirstPgConnection() {
        return PgConnectionImpl.of(postgresCluster.getDataSourceForPrimary(), PgHostImpl.ofUrl(postgresCluster.getFirstContainerJdbcUrl()));
    }

    public PgConnection getSecondPgConnection() {
        return PgConnectionImpl.of(postgresCluster.getDataSourceForStandBy(), PgHostImpl.ofUrl(postgresCluster.getSecondContainerJdbcUrl()));
    }

    public void stopFirstContainer() {
        postgresCluster.stopFirstContainer();
    }
}
