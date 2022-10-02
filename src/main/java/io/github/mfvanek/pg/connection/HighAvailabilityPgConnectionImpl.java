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

import io.github.mfvanek.pg.utils.PgSqlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;

public class HighAvailabilityPgConnectionImpl implements HighAvailabilityPgConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(HighAvailabilityPgConnectionImpl.class);
    private static final Long DEFAULT_DELAY_MILLISECONDS = 30_000L;

    private final AtomicReference<PgConnection> cachedConnectionToPrimary = new AtomicReference<>();
    private final Set<PgConnection> connectionsToAllHostsInCluster;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final PrimaryHostDeterminer primaryHostDeterminer;

    private HighAvailabilityPgConnectionImpl(@Nonnull final PgConnection connectionToPrimary,
                                             @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster,
                                             @Nonnull final PrimaryHostDeterminer primaryHostDeterminer) {
        this.primaryHostDeterminer = Objects.requireNonNull(primaryHostDeterminer);
        Objects.requireNonNull(connectionToPrimary, "connectionToPrimary");
        final Set<PgConnection> defensiveCopy = new HashSet<>(
                Objects.requireNonNull(connectionsToAllHostsInCluster, "connectionsToAllHostsInCluster"));
        PgConnectionValidators.shouldContainsConnectionToPrimary(connectionToPrimary, defensiveCopy);
        this.cachedConnectionToPrimary.set(connectionToPrimary);
        this.connectionsToAllHostsInCluster = Collections.unmodifiableSet(defensiveCopy);
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

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary) {
        final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
        return new HighAvailabilityPgConnectionImpl(connectionToPrimary, Collections.singleton(connectionToPrimary), primaryHostDeterminer);
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster) {
        final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
        final HighAvailabilityPgConnectionImpl highAvailabilityPgConnection = new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster, primaryHostDeterminer);
        highAvailabilityPgConnection.startPrimaryUpdater(DEFAULT_DELAY_MILLISECONDS);
        return highAvailabilityPgConnection;
    }

    @Nonnull
    public static HighAvailabilityPgConnection of(@Nonnull final PgConnection connectionToPrimary,
                                                  @Nonnull final Collection<PgConnection> connectionsToAllHostsInCluster,
                                                  @Nonnull final Long delayMilliseconds) {
        final PrimaryHostDeterminer primaryHostDeterminer = new PrimaryHostDeterminerImpl();
        final HighAvailabilityPgConnectionImpl highAvailabilityPgConnection = new HighAvailabilityPgConnectionImpl(connectionToPrimary, connectionsToAllHostsInCluster, primaryHostDeterminer);
        highAvailabilityPgConnection.startPrimaryUpdater(delayMilliseconds);
        return highAvailabilityPgConnection;
    }

    private void startPrimaryUpdater(@Nonnull final Long delayMilliseconds) {
        Objects.requireNonNull(delayMilliseconds, "delayMilliseconds");
        if (this.getConnectionsToAllHostsInCluster().size() > 1) {
            executorService.scheduleWithFixedDelay(this::updateConnectionToPrimary, delayMilliseconds, delayMilliseconds, TimeUnit.MILLISECONDS);
        } else {
            LOGGER.debug("Single node. There's no point to monitor primary node.");
        }
    }

    private void updateConnectionToPrimary() {
        connectionsToAllHostsInCluster.forEach(pgConnection -> {
            try {
                if (primaryHostDeterminer.isPrimary(pgConnection)) {
                    cachedConnectionToPrimary.set(pgConnection);
                    LOGGER.debug("Current primary is {}", pgConnection.getHost().getPgUrl());
                }
            } catch (PgSqlException e) {
                LOGGER.error("Exception during primary detection for host {}", pgConnection.getHost(), e);
            }
        });
    }
}

