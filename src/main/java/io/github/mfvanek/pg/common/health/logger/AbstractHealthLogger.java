/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.checks.predicates.FilterDuplicatedIndexesByNamePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesByBloatPredicate;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesBySizePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByBloatPredicate;
import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.checks.predicates.FilterTablesBySizePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nonnull;

@SuppressWarnings("PMD.ExcessiveImports")
public abstract class AbstractHealthLogger implements HealthLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHealthLogger.class);

    private final ConnectionCredentials credentials;
    private final HighAvailabilityPgConnectionFactory connectionFactory;
    private final Function<HighAvailabilityPgConnection, DatabaseChecks> databaseChecksFactory;

    @SuppressWarnings("WeakerAccess")
    protected AbstractHealthLogger(@Nonnull final ConnectionCredentials credentials,
                                   @Nonnull final HighAvailabilityPgConnectionFactory connectionFactory,
                                   @Nonnull final Function<HighAvailabilityPgConnection, DatabaseChecks> databaseChecksFactory) {
        this.credentials = Objects.requireNonNull(credentials, "credentials cannot be null");
        this.connectionFactory = Objects.requireNonNull(connectionFactory, "connectionFactory cannot be null");
        this.databaseChecksFactory = Objects.requireNonNull(databaseChecksFactory, "databaseChecksFactory cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public final List<String> logAll(@Nonnull final Exclusions exclusions,
                                     @Nonnull final PgContext pgContext) {
        Objects.requireNonNull(exclusions);
        Objects.requireNonNull(pgContext);
        // The main idea here is to create haPgConnection for a short period of time.
        // This helps to avoid dealing with failover/switch-over situations that occur in real clusters.
        final HighAvailabilityPgConnection haPgConnection = connectionFactory.of(credentials);
        final DatabaseChecks databaseChecks = databaseChecksFactory.apply(haPgConnection);
        final List<String> logResult = new ArrayList<>();
        logResult.add(logInvalidIndexes(databaseChecks, pgContext));
        logResult.add(logDuplicatedIndexes(databaseChecks, exclusions, pgContext));
        logResult.add(logIntersectedIndexes(databaseChecks, exclusions, pgContext));
        logResult.add(logUnusedIndexes(databaseChecks, exclusions, pgContext));
        logResult.add(logForeignKeysNotCoveredWithIndex(databaseChecks, pgContext));
        logResult.add(logTablesWithMissingIndexes(databaseChecks, exclusions, pgContext));
        logResult.add(logTablesWithoutPrimaryKey(databaseChecks, exclusions, pgContext));
        logResult.add(logIndexesWithNullValues(databaseChecks, exclusions, pgContext));
        logResult.add(logIndexesBloat(databaseChecks, exclusions, pgContext));
        logResult.add(logTablesBloat(databaseChecks, exclusions, pgContext));
        return logResult;
    }

    protected abstract String writeToLog(@Nonnull LoggingKey key, int value);

    @Nonnull
    private String writeZeroToLog(@Nonnull final LoggingKey key) {
        return writeToLog(key, 0);
    }

    @Nonnull
    private String logInvalidIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                     @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<Index> check = databaseChecks.getCheck(Diagnostic.INVALID_INDEXES, Index.class);
        final List<Index> invalidIndexes = check.check(pgContext);
        final LoggingKey key = SimpleLoggingKey.INVALID_INDEXES;
        if (CollectionUtils.isNotEmpty(invalidIndexes)) {
            LOGGER.error("There are invalid indexes in the database {}", invalidIndexes);
            return writeToLog(key, invalidIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logDuplicatedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                        @Nonnull final Exclusions exclusions,
                                        @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<DuplicatedIndexes> check = databaseChecks.getCheck(Diagnostic.DUPLICATED_INDEXES, DuplicatedIndexes.class);
        final List<DuplicatedIndexes> duplicatedIndexes = check.check(pgContext, FilterDuplicatedIndexesByNamePredicate.of(exclusions.getDuplicatedIndexesExclusions()));
        final LoggingKey key = SimpleLoggingKey.DUPLICATED_INDEXES;
        if (CollectionUtils.isNotEmpty(duplicatedIndexes)) {
            LOGGER.warn("There are duplicated indexes in the database {}", duplicatedIndexes);
            return writeToLog(key, duplicatedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIntersectedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                         @Nonnull final Exclusions exclusions,
                                         @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<DuplicatedIndexes> check = databaseChecks.getCheck(Diagnostic.INTERSECTED_INDEXES, DuplicatedIndexes.class);
        final List<DuplicatedIndexes> intersectedIndexes = check.check(pgContext, FilterDuplicatedIndexesByNamePredicate.of(exclusions.getIntersectedIndexesExclusions()));
        final LoggingKey key = SimpleLoggingKey.INTERSECTED_INDEXES;
        if (CollectionUtils.isNotEmpty(intersectedIndexes)) {
            LOGGER.warn("There are intersected indexes in the database {}", intersectedIndexes);
            return writeToLog(key, intersectedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logUnusedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                    @Nonnull final Exclusions exclusions,
                                    @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<UnusedIndex> check = databaseChecks.getCheck(Diagnostic.UNUSED_INDEXES, UnusedIndex.class);
        final List<UnusedIndex> unusedIndexes = check.check(pgContext, FilterIndexesBySizePredicate.of(exclusions.getIndexSizeThresholdInBytes())
                .and(FilterIndexesByNamePredicate.of(exclusions.getUnusedIndexesExclusions())));
        final LoggingKey key = SimpleLoggingKey.UNUSED_INDEXES;
        if (CollectionUtils.isNotEmpty(unusedIndexes)) {
            LOGGER.warn("There are unused indexes in the database {}", unusedIndexes);
            return writeToLog(key, unusedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logForeignKeysNotCoveredWithIndex(@Nonnull final DatabaseChecks databaseChecks,
                                                     @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<ForeignKey> check = databaseChecks.getCheck(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX, ForeignKey.class);
        final List<ForeignKey> foreignKeys = check.check(pgContext);
        final LoggingKey key = SimpleLoggingKey.FOREIGN_KEYS;
        if (CollectionUtils.isNotEmpty(foreignKeys)) {
            LOGGER.warn("There are foreign keys without index in the database {}", foreignKeys);
            return writeToLog(key, foreignKeys.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithMissingIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                               @Nonnull final Exclusions exclusions,
                                               @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<TableWithMissingIndex> check = databaseChecks.getCheck(Diagnostic.TABLES_WITH_MISSING_INDEXES, TableWithMissingIndex.class);
        final List<TableWithMissingIndex> tablesWithMissingIndexes = check.check(pgContext, FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())
                .and(FilterTablesByNamePredicate.of(exclusions.getTablesWithMissingIndexesExclusions())));
        final LoggingKey key = SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES;
        if (CollectionUtils.isNotEmpty(tablesWithMissingIndexes)) {
            LOGGER.warn("There are tables with missing indexes in the database {}", tablesWithMissingIndexes);
            return writeToLog(key, tablesWithMissingIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithoutPrimaryKey(@Nonnull final DatabaseChecks databaseChecks,
                                              @Nonnull final Exclusions exclusions,
                                              @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<Table> check = databaseChecks.getCheck(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY, Table.class);
        final List<Table> tablesWithoutPrimaryKey = check.check(pgContext, FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())
                .and(FilterTablesByNamePredicate.of(exclusions.getTablesWithoutPrimaryKeyExclusions())));
        final LoggingKey key = SimpleLoggingKey.TABLES_WITHOUT_PK;
        if (CollectionUtils.isNotEmpty(tablesWithoutPrimaryKey)) {
            LOGGER.warn("There are tables without primary key in the database {}", tablesWithoutPrimaryKey);
            return writeToLog(key, tablesWithoutPrimaryKey.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIndexesWithNullValues(@Nonnull final DatabaseChecks databaseChecks,
                                            @Nonnull final Exclusions exclusions,
                                            @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<IndexWithNulls> check = databaseChecks.getCheck(Diagnostic.INDEXES_WITH_NULL_VALUES, IndexWithNulls.class);
        final List<IndexWithNulls> indexesWithNullValues = check.check(pgContext, FilterIndexesByNamePredicate.of(exclusions.getIndexesWithNullValuesExclusions()));
        final LoggingKey key = SimpleLoggingKey.INDEXES_WITH_NULLS;
        if (CollectionUtils.isNotEmpty(indexesWithNullValues)) {
            LOGGER.warn("There are indexes with null values in the database {}", indexesWithNullValues);
            return writeToLog(key, indexesWithNullValues.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIndexesBloat(@Nonnull final DatabaseChecks databaseChecks,
                                   @Nonnull final Exclusions exclusions,
                                   @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<IndexWithBloat> check = databaseChecks.getCheck(Diagnostic.BLOATED_INDEXES, IndexWithBloat.class);
        final List<IndexWithBloat> indexesWithBloat = check.check(pgContext,
                FilterIndexesByBloatPredicate.of(exclusions.getIndexBloatSizeThresholdInBytes(), exclusions.getIndexBloatPercentageThreshold())
                        .and(FilterIndexesBySizePredicate.of(exclusions.getIndexSizeThresholdInBytes())));
        final LoggingKey key = SimpleLoggingKey.INDEXES_BLOAT;
        if (CollectionUtils.isNotEmpty(indexesWithBloat)) {
            LOGGER.warn("There are indexes with bloat in the database {}", indexesWithBloat);
            return writeToLog(key, indexesWithBloat.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesBloat(@Nonnull final DatabaseChecks databaseChecks,
                                  @Nonnull final Exclusions exclusions,
                                  @Nonnull final PgContext pgContext) {
        final DatabaseCheckOnCluster<TableWithBloat> check = databaseChecks.getCheck(Diagnostic.BLOATED_TABLES, TableWithBloat.class);
        final List<TableWithBloat> tablesWithBloat = check.check(pgContext,
                FilterTablesByBloatPredicate.of(exclusions.getTableBloatSizeThresholdInBytes(), exclusions.getTableBloatPercentageThreshold())
                        .and(FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())));
        final LoggingKey key = SimpleLoggingKey.TABLES_BLOAT;
        if (CollectionUtils.isNotEmpty(tablesWithBloat)) {
            LOGGER.warn("There are tables with bloat in the database {}", tablesWithBloat);
            return writeToLog(key, tablesWithBloat.size());
        }
        return writeZeroToLog(key);
    }
}
