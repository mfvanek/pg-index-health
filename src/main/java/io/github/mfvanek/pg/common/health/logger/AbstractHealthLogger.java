/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
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
        logResult.add(logTablesWithoutDescription(databaseChecks, pgContext));
        logResult.add(logColumnsWithoutDescription(databaseChecks, pgContext));
        logResult.add(logColumnsWithJsonType(databaseChecks, pgContext));
        logResult.add(logColumnsWithSerialTypes(databaseChecks, pgContext));
        logResult.add(logFunctionsWithoutDescription(databaseChecks, pgContext));
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
        return logCheckResult(databaseChecks.getCheck(Diagnostic.INVALID_INDEXES, Index.class),
                c -> true, pgContext, SimpleLoggingKey.INVALID_INDEXES);
    }

    @Nonnull
    private String logDuplicatedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                        @Nonnull final Exclusions exclusions,
                                        @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.DUPLICATED_INDEXES, DuplicatedIndexes.class),
                FilterDuplicatedIndexesByNamePredicate.of(exclusions.getDuplicatedIndexesExclusions()), pgContext, SimpleLoggingKey.DUPLICATED_INDEXES);
    }

    @Nonnull
    private String logIntersectedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                         @Nonnull final Exclusions exclusions,
                                         @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.INTERSECTED_INDEXES, DuplicatedIndexes.class),
                FilterDuplicatedIndexesByNamePredicate.of(exclusions.getIntersectedIndexesExclusions()), pgContext, SimpleLoggingKey.INTERSECTED_INDEXES);
    }

    @Nonnull
    private String logUnusedIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                    @Nonnull final Exclusions exclusions,
                                    @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.UNUSED_INDEXES, UnusedIndex.class),
                FilterIndexesBySizePredicate.of(exclusions.getIndexSizeThresholdInBytes())
                        .and(FilterIndexesByNamePredicate.of(exclusions.getUnusedIndexesExclusions())), pgContext, SimpleLoggingKey.UNUSED_INDEXES);
    }

    @Nonnull
    private String logForeignKeysNotCoveredWithIndex(@Nonnull final DatabaseChecks databaseChecks,
                                                     @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX, ForeignKey.class),
                c -> true, pgContext, SimpleLoggingKey.FOREIGN_KEYS_WITHOUT_INDEX);
    }

    @Nonnull
    private String logTablesWithMissingIndexes(@Nonnull final DatabaseChecks databaseChecks,
                                               @Nonnull final Exclusions exclusions,
                                               @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.TABLES_WITH_MISSING_INDEXES, TableWithMissingIndex.class),
                FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())
                        .and(FilterTablesByNamePredicate.of(exclusions.getTablesWithMissingIndexesExclusions())), pgContext, SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES);
    }

    @Nonnull
    private String logTablesWithoutPrimaryKey(@Nonnull final DatabaseChecks databaseChecks,
                                              @Nonnull final Exclusions exclusions,
                                              @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY, Table.class),
                FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())
                        .and(FilterTablesByNamePredicate.of(exclusions.getTablesWithoutPrimaryKeyExclusions())), pgContext, SimpleLoggingKey.TABLES_WITHOUT_PRIMARY_KEY);
    }

    @Nonnull
    private String logIndexesWithNullValues(@Nonnull final DatabaseChecks databaseChecks,
                                            @Nonnull final Exclusions exclusions,
                                            @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.INDEXES_WITH_NULL_VALUES, IndexWithNulls.class),
                FilterIndexesByNamePredicate.of(exclusions.getIndexesWithNullValuesExclusions()), pgContext, SimpleLoggingKey.INDEXES_WITH_NULL_VALUES);
    }

    @Nonnull
    private String logIndexesBloat(@Nonnull final DatabaseChecks databaseChecks,
                                   @Nonnull final Exclusions exclusions,
                                   @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.BLOATED_INDEXES, IndexWithBloat.class),
                FilterIndexesByBloatPredicate.of(exclusions.getIndexBloatSizeThresholdInBytes(), exclusions.getIndexBloatPercentageThreshold())
                        .and(FilterIndexesBySizePredicate.of(exclusions.getIndexSizeThresholdInBytes())), pgContext, SimpleLoggingKey.BLOATED_INDEXES);
    }

    @Nonnull
    private String logTablesBloat(@Nonnull final DatabaseChecks databaseChecks,
                                  @Nonnull final Exclusions exclusions,
                                  @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.BLOATED_TABLES, TableWithBloat.class),
                FilterTablesByBloatPredicate.of(exclusions.getTableBloatSizeThresholdInBytes(), exclusions.getTableBloatPercentageThreshold())
                        .and(FilterTablesBySizePredicate.of(exclusions.getTableSizeThresholdInBytes())), pgContext, SimpleLoggingKey.BLOATED_TABLES);
    }

    @Nonnull
    private String logTablesWithoutDescription(@Nonnull final DatabaseChecks databaseChecks,
                                               @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.TABLES_WITHOUT_DESCRIPTION, Table.class),
                c -> true, pgContext, SimpleLoggingKey.TABLES_WITHOUT_DESCRIPTION);
    }

    @Nonnull
    private String logColumnsWithoutDescription(@Nonnull final DatabaseChecks databaseChecks,
                                                @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION, Column.class),
                c -> true, pgContext, SimpleLoggingKey.COLUMNS_WITHOUT_DESCRIPTION);
    }

    @Nonnull
    private String logColumnsWithJsonType(@Nonnull final DatabaseChecks databaseChecks,
                                          @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.COLUMNS_WITH_JSON_TYPE, Column.class),
                c -> true, pgContext, SimpleLoggingKey.COLUMNS_WITH_JSON_TYPE);
    }

    @Nonnull
    private String logColumnsWithSerialTypes(@Nonnull final DatabaseChecks databaseChecks,
                                             @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.COLUMNS_WITH_SERIAL_TYPES, ColumnWithSerialType.class),
                c -> true, pgContext, SimpleLoggingKey.COLUMNS_WITH_SERIAL_TYPES);
    }

    @Nonnull
    private String logFunctionsWithoutDescription(@Nonnull final DatabaseChecks databaseChecks,
                                                  @Nonnull final PgContext pgContext) {
        return logCheckResult(databaseChecks.getCheck(Diagnostic.FUNCTIONS_WITHOUT_DESCRIPTION, StoredFunction.class),
                c -> true, pgContext, SimpleLoggingKey.FUNCTIONS_WITHOUT_DESCRIPTION);
    }

    @Nonnull
    private <T extends DbObject> String logCheckResult(@Nonnull final DatabaseCheckOnCluster<T> check,
                                                       @Nonnull final Predicate<? super T> exclusionsFilter,
                                                       @Nonnull final PgContext pgContext,
                                                       @Nonnull final LoggingKey key) {
        final List<T> checkResult = check.check(pgContext, exclusionsFilter);
        if (!checkResult.isEmpty()) {
            LOGGER.warn("There are {} in the database {}", key.getDescription(), checkResult);
            return writeToLog(key, checkResult.size());
        }
        return writeZeroToLog(key);
    }
}
