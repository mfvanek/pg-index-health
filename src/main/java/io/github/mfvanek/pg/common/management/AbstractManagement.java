/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.management;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public abstract class AbstractManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManagement.class);

    private final HighAvailabilityPgConnection haPgConnection;

    protected AbstractManagement(@Nonnull final HighAvailabilityPgConnection haPgConnection) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection");
    }

    @Nonnull
    protected PgHost getPrimaryHost() {
        // Primary host may change its location within the cluster due to failover or switchover.
        // So we need to ensure where the primary is.
        return haPgConnection.getConnectionToPrimary().getHost();
    }

    protected <M, R> R doOnPrimary(@Nonnull final Map<PgHost, M> maintenances, @Nonnull final Function<M, R> func) {
        final PgHost primaryHost = getPrimaryHost();
        final M maintenance = maintenances.get(primaryHost);
        LOGGER.debug("Going to execute on primary host [{}]", primaryHost.getName());
        return func.apply(maintenance);
    }

    protected <M, T, R> R doOnPrimary(@Nonnull final Map<PgHost, M> maintenances, @Nonnull final BiFunction<M, T, R> func, @Nonnull final T arg) {
        final PgHost primaryHost = getPrimaryHost();
        final M maintenance = maintenances.get(primaryHost);
        LOGGER.debug("Going to execute on primary host [{}]", primaryHost.getName());
        return func.apply(maintenance, arg);
    }

    protected static <T> T doOnHost(@Nonnull final PgHost host, @Nonnull final Supplier<T> action) {
        LOGGER.debug("Going to execute on host {}", host.getName());
        return action.get();
    }
}
