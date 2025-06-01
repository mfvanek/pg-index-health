/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a connection to a high availability cluster (with set of primary host and replicas).
 *
 * @author Ivan Vakhrushev
 * @author Alexey Antipin
 * @see HighAvailabilityPgConnection
 */
public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private static final Logger LOGGER = Logger.getLogger(HighAvailabilityPgConnectionImpl.class.getName());
    private static final long DEFAULT_PRIMARY_REFRESH_INTERVAL_MILLISECONDS = 30_000L;

    private final AtomicReference<PgConnection> cachedConnectionToPrimary = new AtomicReference<>();
    private final Set<PgConnection> connectionsToAllHostsInCluster;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PrimaryHostDeterminer primaryHostDeterminer;

    private HighAvailabilityPgConnectionImpl(final PgConnection connectionToPrimary,
                                             final Collection<PgConnection> connectionsToAllHostsInCluster,
                                             final PrimaryHostDeterminer primaryHostDeterminer) {
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
        Objects.requireNonNull(connectionToPrimary, "connectionToPrimary");
        final Set<PgConnection> defensiveCopy = Set.copyOf(Objects.requireNonNull(connectionsToAllHostsInCluster, "connectionsToAllHostsInCluster"));
        shouldContainsConnectionToPrimary(connectionToPrimary, defensiveCopy);
        this.cachedConnectionToPrimary.set(connectionToPrimary);
        this.connectionsToAllHostsInCluster = defensiveCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("NullAway")
    public PgConnection getConnectionToPrimary() {
        return cachedConnectionToPrimary.get(); // always not null
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<PgConnection> getConnectionsToAllHostsInCluster() {
        return connectionsToAllHostsInCluster;
    }

    /**
     * Constructs a {@code HighAvailabilityPgConnection} object with the given {@code PgConnection}.
     *
     * @param connectionToPrimary connection to the primary host in the single-node cluster.
     * @return {@code HighAvailabilityPgConnection}
     */
    public static HighAvailabilityPgConnection of(final PgConnection connectionToPrimary) {
        return of(connectionToPrimary, Set.of(connectionToPrimary));
    }

    /**
     * Constructs a {@code HighAvailabilityPgConnection} object with the given connections to primary and replicas.
     *
     * @param connectionToPrimary            connection to the primary host in the cluster.
     * @param connectionsToAllHostsInCluster connections to all replicas in the cluster.
     * @return {@code HighAvailabilityPgConnection}
     */
    public static HighAvailabilityPgConnection of(final PgConnection connectionToPrimary,
                                                  final Collection<PgConnection> connectionsToAllHostsInCluster) {
        return of(connectionToPrimary, connectionsToAllHostsInCluster, DEFAULT_PRIMARY_REFRESH_INTERVAL_MILLISECONDS);
    }

    /**
     * Constructs a {@code HighAvailabilityPgConnection} object with the given connections to primary and replicas and a refresh interval.
     *
     * @param connectionToPrimary                connection to the primary host in the cluster.
     * @param connectionsToAllHostsInCluster     connections to all replicas in the cluster.
     * @param primaryRefreshIntervalMilliseconds time interval in milliseconds to refresh connection to the primary host.
     * @return {@code HighAvailabilityPgConnection}
     */
    public static HighAvailabilityPgConnection of(final PgConnection connectionToPrimary,
                                                  final Collection<PgConnection> connectionsToAllHostsInCluster,
                                                  final long primaryRefreshIntervalMilliseconds) {
        final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
        final HighAvailabilityPgConnectionImpl highAvailabilityPgConnection = new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster, primaryHostDeterminer);
        highAvailabilityPgConnection.startPrimaryUpdater(primaryRefreshIntervalMilliseconds);
        return highAvailabilityPgConnection;
    }

    private void startPrimaryUpdater(final long primaryRefreshIntervalMilliseconds) {
        if (this.getConnectionsToAllHostsInCluster().size() >= 2) {
            executorService.scheduleWithFixedDelay(this::updateConnectionToPrimary, primaryRefreshIntervalMilliseconds, primaryRefreshIntervalMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            LOGGER.fine("Single node. There's no point to monitor primary node.");
        }
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private void updateConnectionToPrimary() {
        connectionsToAllHostsInCluster.forEach(pgConnection -> {
            try {
                if (primaryHostDeterminer.isPrimary(pgConnection)) {
                    cachedConnectionToPrimary.set(pgConnection);
                    LOGGER.fine(() -> "Current primary is " + pgConnection.getHost().getPgUrl());
                }
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, e, () -> "Exception during primary detection for host " + pgConnection.getHost());
            }
        });
    }

    private static void shouldContainsConnectionToPrimary(final PgConnection connectionToPrimary,
                                                          final Set<PgConnection> connectionsToAllHostsInCluster) {
        if (!connectionsToAllHostsInCluster.contains(connectionToPrimary)) {
            throw new IllegalArgumentException("connectionsToAllHostsInCluster have to contain a connection to the primary");
        }
    }
}
