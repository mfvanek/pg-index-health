/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.logger;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.factory.ConnectionCredentials;
import io.github.mfvanek.pg.connection.factory.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.predicates.SkipBloatUnderThresholdPredicate;
import io.github.mfvanek.pg.model.predicates.SkipBySequenceNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallIndexesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public abstract class AbstractHealthLogger implements HealthLogger {

    private static final Logger LOGGER = Logger.getLogger(AbstractHealthLogger.class.getName());

    private final ConnectionCredentials credentials;
    private final HighAvailabilityPgConnectionFactory connectionFactory;
    private final Function<HighAvailabilityPgConnection, DatabaseChecksOnCluster> databaseChecksFactory;

    @SuppressWarnings("WeakerAccess")
    protected AbstractHealthLogger(final ConnectionCredentials credentials,
                                   final HighAvailabilityPgConnectionFactory connectionFactory,
                                   final Function<HighAvailabilityPgConnection, DatabaseChecksOnCluster> databaseChecksFactory) {
        this.credentials = Objects.requireNonNull(credentials, "credentials cannot be null");
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "connectionFactory cannot be null");
        this.databaseChecksFactory = Objects.requireNonNull(databaseChecksFactory, "databaseChecksFactory cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<String> logAll(final Exclusions exclusions,
                                     final PgContext pgContext) {
        Objects.requireNonNull(exclusions);
        Objects.requireNonNull(pgContext);
        // The main idea here is to create haPgConnection for a short period of time.
        // This helps to avoid dealing with failover/switch-over situations that occur in real clusters.
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of(credentials);
        final DatabaseChecksOnCluster databaseChecksOnCluster = databaseChecksFactory.apply(haPgConnection);
        final Predicate<DbObject> jointFilters = prepareFilters(exclusions, pgContext);
        final List<String> logResult = new ArrayList<>();
        for (final DatabaseCheckOnCluster<? extends DbObject> check : databaseChecksOnCluster.getAll()) {
            final LoggingKey key = SimpleLoggingKeyAdapter.of(check.getDiagnostic());
            final List<? extends DbObject> checkResult = check.check(pgContext, jointFilters);
            if (checkResult.isEmpty()) {
                logResult.add(writeZeroToLog(key));
            } else {
                LOGGER.warning(() -> String.format(Locale.ROOT, "There are %s in the database %s", key.getDescription(), checkResult));
                logResult.add(writeToLog(key, checkResult.size()));
            }
        }
        return logResult;
    }

    private Predicate<DbObject> prepareFilters(final Exclusions exclusions, final PgContext pgContext) {
        return SkipTablesByNamePredicate.of(pgContext, exclusions.getTableNameExclusions())
            .and(SkipIndexesByNamePredicate.of(pgContext, exclusions.getIndexNameExclusions()))
            .and(SkipBySequenceNamePredicate.of(pgContext, exclusions.getSequenceNameExclusions()))
            .and(SkipBloatUnderThresholdPredicate.of(exclusions.getBloatSizeThresholdInBytes(), exclusions.getBloatPercentageThreshold()))
            .and(SkipSmallTablesPredicate.of(exclusions.getTableSizeThresholdInBytes()))
            .and(SkipSmallIndexesPredicate.of(exclusions.getIndexSizeThresholdInBytes()));
    }

    protected abstract String writeToLog(LoggingKey key, int value);

    private String writeZeroToLog(final LoggingKey key) {
        return writeToLog(key, 0);
    }
}
