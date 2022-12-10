/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

/**
 * Implementation of a connection to a high availability cluster (with set of primary host and replicas).
 *
 * @author Ivan Vakhrushev
 * @author Alexey Antipin
 * @see HighAvailabilityPgConnection
 */
public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighAvailabilityPgConnectionImpl.class);
    private static final long DEFAULT_PRIMARY_REFRESH_INTERVAL_MILLISECONDS = 30_000L;

    private final AtomicReference<PgConnection> cachedConnectionToPrimary = new AtomicReference<>();
    private final Set<PgConnection> connectionsToAllHostsInCluster;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PrimaryHostDeterminer primaryHostDeterminer;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToPrimary,
                                             @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster,
                                             @Nonnull final PrimaryHostDeterminer primaryHostDeterminer) {
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
        Objects.requireNonNull(connectionToPrimary, "connectionToPrimary");
        final Set<PgConnection> defensiveCopy = Set.copyOf(Objects.requireNonNull(connectionsToAllHostsInCluster, "connectionsToAllHostsInCluster"));
        PgConnectionValidators.shouldContainsConnectionToPrimary(connectionToPrimary, defensiveCopy);
        this.cachedConnectionToPrimary.set(connectionToPrimary);
        this.connectionsToAllHostsInCluster = defensiveCopy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public PgConnection getConnectionToPrimary() {
        return cachedConnectionToPrimary.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Set<PgConnection> getConnectionsToAllHostsInCluster() {
        return connectionsToAllHostsInCluster;
    }

    /**
     * Constructs a {@code HighAvailabilityPgConnection} object with the given {@code PgConnection}.
     *
     * @param connectionToPrimary connection to the primary host in the single-node cluster.
     * @return {@code HighAvailabilityPgConnection}
     */
    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary) {
        return of(connectionToPrimary, Set.of(connectionToPrimary));
    }

    /**
     * Constructs a {@code HighAvailabilityPgConnection} object with the given connections to primary and replicas.
     *
     * @param connectionToPrimary            connection to the primary host in the cluster.
     * @param connectionsToAllHostsInCluster connections to all replicas in the cluster.
     * @return {@code HighAvailabilityPgConnection}
     */
    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster) {
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
    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster,
                                                  final long primaryRefreshIntervalMilliseconds) {
        final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
        final HighAvailabilityPgConnectionImpl highAvailabilityPgConnection = new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster, primaryHostDeterminer);
        highAvailabilityPgConnection.startPrimaryUpdater(primaryRefreshIntervalMilliseconds);
        return highAvailabilityPgConnection;
    }

    private void startPrimaryUpdater(final long primaryRefreshIntervalMilliseconds) {
        if (this.getConnectionsToAllHostsInCluster().size() > 1) {
            executorService.scheduleWithFixedDelay(this::updateConnectionToPrimary, primaryRefreshIntervalMilliseconds, primaryRefreshIntervalMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            LOGGER.debug("Single node. There's no point to monitor primary node.");
        }
    }

    @SuppressWarnings("checkstyle:IllegalCatch")
    private void updateConnectionToPrimary() {
        connectionsToAllHostsInCluster.forEach(pgConnection -> {
            try {
                if (primaryHostDeterminer.isPrimary(pgConnection)) {
                    cachedConnectionToPrimary.set(pgConnection);
                    LOGGER.debug("Current primary is {}", pgConnection.getHost().getPgUrl());
                }
            } catch (Exception e) {
                LOGGER.warn("Exception during primary detection for host {}", pgConnection.getHost(), e);
            }
        });
    }
}
