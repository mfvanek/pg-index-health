/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.host.PgHost;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * An abstract class for all database checks performed on entire cluster.
 *
 * @param <T> represents an object in a database associated with a table
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
abstract class AbstractCheckOnCluster<T extends DbObject> implements DatabaseCheckOnCluster<T> {

    private static final Logger LOGGER = Logger.getLogger(AbstractCheckOnCluster.class.getName());

    private final HighAvailabilityPgConnection haPgConnection;
    private final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory;
    private final Map<PgHost, DatabaseCheckOnHost<T>> checksOnHosts;
    private final @Nullable Function<List<List<T>>, List<T>> acrossClusterResultsMapper;

    protected AbstractCheckOnCluster(final HighAvailabilityPgConnection haPgConnection,
                                     final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory) {
        this(haPgConnection, checkOnHostFactory, null);
    }

    protected AbstractCheckOnCluster(final HighAvailabilityPgConnection haPgConnection,
                                     final Function<PgConnection, DatabaseCheckOnHost<T>> checkOnHostFactory,
                                     @Nullable final Function<List<List<T>>, List<T>> acrossClusterResultsMapper) {
        this.haPgConnection = Objects.requireNonNull(haPgConnection, "haPgConnection cannot be null");
        this.checkOnHostFactory = Objects.requireNonNull(checkOnHostFactory, "checkOnHostFactory cannot be null");
        this.checksOnHosts = new HashMap<>();
        this.acrossClusterResultsMapper = acrossClusterResultsMapper;
        final DatabaseCheckOnHost<T> checkOnPrimary = computeCheckForPrimaryIfNeed();
        if (acrossClusterResultsMapper == null && checkOnPrimary.getDiagnostic().isAcrossCluster()) {
            throw new IllegalArgumentException("acrossClusterResultsMapper cannot be null for diagnostic " + checkOnPrimary.getDiagnostic());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        return computeCheckForPrimaryIfNeed().getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Diagnostic getDiagnostic() {
        return computeCheckForPrimaryIfNeed().getDiagnostic();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<T> check(final PgContext pgContext, final Predicate<? super T> exclusionsFilter) {
        if (getDiagnostic().isAcrossCluster()) {
            return executeOnCluster(pgContext, exclusionsFilter);
        }
        return executeOnPrimary(pgContext, exclusionsFilter);
    }

    protected void doBeforeExecuteOnHost(final PgConnection connectionToHost) {
        LOGGER.fine(() -> "Going to execute on host " + connectionToHost.getHost().getName());
    }

    private DatabaseCheckOnHost<T> computeCheckForPrimaryIfNeed() {
        return computeCheckForHostIfNeed(haPgConnection.getConnectionToPrimary());
    }

    private DatabaseCheckOnHost<T> computeCheckForHostIfNeed(final PgConnection connectionToHost) {
        return checksOnHosts.computeIfAbsent(connectionToHost.getHost(), h -> checkOnHostFactory.apply(connectionToHost));
    }

    private List<T> executeOnPrimary(final PgContext pgContext, final Predicate<? super T> exclusionsFilter) {
        final DatabaseCheckOnHost<T> checkOnPrimary = computeCheckForPrimaryIfNeed();
        LOGGER.fine(() -> "Going to execute on primary host " + checkOnPrimary.getHost().getName());
        return checkOnPrimary.check(pgContext, exclusionsFilter);
    }

    @SuppressWarnings("NullAway")
    private List<T> executeOnCluster(final PgContext pgContext, final Predicate<? super T> exclusionsFilter) {
        final List<List<T>> acrossClusterResults = new ArrayList<>();
        for (final PgConnection pgConnection : haPgConnection.getConnectionsToAllHostsInCluster()) {
            doBeforeExecuteOnHost(pgConnection);
            final List<T> resultsFromHost = executeOnHost(pgConnection, pgContext, exclusionsFilter);
            acrossClusterResults.add(resultsFromHost);
        }
        return acrossClusterResultsMapper.apply(acrossClusterResults); // acrossClusterResultsMapper cannot be null here
    }

    private List<T> executeOnHost(final PgConnection connectionToHost,
                                  final PgContext pgContext,
                                  final Predicate<? super T> exclusionsFilter) {
        final DatabaseCheckOnHost<T> checkOnHost = computeCheckForHostIfNeed(connectionToHost);
        return checkOnHost.check(pgContext, exclusionsFilter);
    }
}
