/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgHost;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.context.PgContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An abstract class for all database checks performed on entire cluster.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
abstract class AbstractCheckOnCluster<T extends DbObject> implements DatabaseCheckOnCluster<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCheckOnCluster.class);

    private final HighAvailabilityPgConnection haPgConnection;
    private final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory;
    private final Map<PgHost, DatabaseCheckOnHost<T>> checksOnHosts;
    private final Function<List<List<T>>, List<T>> acrossClusterResultsMapper;

    protected AbstractCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                     @Nonnull final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory) {
        this(haPgConnection, checkOnHostFactory, null);
    }

    protected AbstractCheckOnCluster(@Nonnull final HighAvailabilityPgConnection haPgConnection,
                                     @Nonnull final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory,
                                     @Nullable final Function<List<List<T>>, List<T>> acrossClusterResultsMapper) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection cannot be null");
        this.checkOnHostFactory = Objects.requireNonNull(checkOnHostFactory, "checkOnHostFactory cannot be null");
        this.checksOnHosts = new HashMap<>();
        this.acrossClusterResultsMapper = acrossClusterResultsMapper;
        final DatabaseCheckOnHost<T> checkOnPrimary = computeCheckForPrimaryIfNeed();
        if (checkOnPrimary.getDiagnostic().isAcrossCluster() && Objects.isNull(acrossClusterResultsMapper)) {
            throw new IllegalArgumentException("acrossClusterResultsMapper cannot be null for diagnostic " + checkOnPrimary.getDiagnostic());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Class<T> getType() {
        return computeCheckForPrimaryIfNeed().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final Diagnostic getDiagnostic() {
        return computeCheckForPrimaryIfNeed().getDiagnostic();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final List<T> check(@Nonnull final PgContext pgContext, @Nonnull final Predicate<? super T> exclusionsFilter) {
        if (getDiagnostic().isAcrossCluster()) {
            return executeOnCluster(pgContext, exclusionsFilter);
        }
        return executeOnPrimary(pgContext, exclusionsFilter);
    }

    protected void doBeforeExecuteOnHost(@Nonnull final PgConnection connectionToHost) {
        LOGGER.debug("Going to execute on host {}", connectionToHost.getHost().getName());
    }

    @Nonnull
    private DatabaseCheckOnHost<T> computeCheckForPrimaryIfNeed() {
        return computeCheckForHostIfNeed(haPgConnection.getConnectionToPrimary());
    }

    @Nonnull
    private DatabaseCheckOnHost<T> computeCheckForHostIfNeed(@Nonnull final PgConnection connectionToHost) {
        return checksOnHosts.computeIfAbsent(connectionToHost.getHost(), h -> checkOnHostFactory.apply(connectionToHost));
    }

    @Nonnull
    private List<T> executeOnPrimary(@Nonnull final PgContext pgContext, @Nonnull final Predicate<? super T> exclusionsFilter) {
        final DatabaseCheckOnHost<T> checkOnPrimary = computeCheckForPrimaryIfNeed();
        LOGGER.debug("Going to execute on primary host {}", checkOnPrimary.getHost().getName());
        return checkOnPrimary.check(pgContext, exclusionsFilter);
    }

    @Nonnull
    private List<T> executeOnCluster(@Nonnull final PgContext pgContext, @Nonnull final Predicate<? super T> exclusionsFilter) {
        final List<List<T>> acrossClusterResults = new ArrayList<>();
        for (final PgConnection pgConnection : haPgConnection.getConnectionsToAllHostsInCluster()) {
            doBeforeExecuteOnHost(pgConnection);
            final List<T> resultsFromHost = executeOnHost(pgConnection, pgContext, exclusionsFilter);
            acrossClusterResults.add(resultsFromHost);
        }
        return acrossClusterResultsMapper.apply(acrossClusterResults);
    }

    @Nonnull
    private List<T> executeOnHost(@Nonnull final PgConnection connectionToHost,
                                  @Nonnull final PgContext pgContext,
                                  @Nonnull final Predicate<? super T> exclusionsFilter) {
        final DatabaseCheckOnHost<T> checkOnHost = computeCheckForHostIfNeed(connectionToHost);
        return checkOnHost.check(pgContext, exclusionsFilter);
    }
}
