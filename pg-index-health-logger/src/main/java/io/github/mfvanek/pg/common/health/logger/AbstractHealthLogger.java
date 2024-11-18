/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.predicates.SkipBloatUnderThresholdPredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallIndexesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipSmallTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

@SuppressWarnings({"PMD.ExcessiveImports", "checkstyle:ExecutableStatementCount"})
public abstract class AbstractHealthLogger implements HealthLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHealthLogger.class);

    private final ConnectionCredentials credentials;
    private final HighAvailabilityPgConnectionFactory connectionFactory;
    private final Function<HighAvailabilityPgConnection, DatabaseChecks> databaseChecksFactory;
    private final AtomicReference<DatabaseChecks> databaseChecksHolder = new AtomicReference<>();
    private final AtomicReference<PgContext> pgContextHolder = new AtomicReference<>();

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
        try {
            pgContextHolder.set(pgContext);
            databaseChecksHolder.set(databaseChecksFactory.apply(haPgConnection));
            final List<String> logResult = new ArrayList<>();
            logResult.add(logCheckResult(Diagnostic.INVALID_INDEXES, Index.class, SimpleLoggingKey.INVALID_INDEXES));
            logResult.add(logDuplicatedIndexes(exclusions));
            logResult.add(logIntersectedIndexes(exclusions));
            logResult.add(logUnusedIndexes(exclusions));
            logResult.add(logCheckResult(Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX, ForeignKey.class, SimpleLoggingKey.FOREIGN_KEYS_WITHOUT_INDEX));
            logResult.add(logTablesWithMissingIndexes(exclusions));
            logResult.add(logTablesWithoutPrimaryKey(exclusions));
            logResult.add(logIndexesWithNullValues(exclusions));
            logResult.add(logIndexesBloat(exclusions));
            logResult.add(logTablesBloat(exclusions));
            logResult.add(logCheckResult(Diagnostic.TABLES_WITHOUT_DESCRIPTION, Table.class, SimpleLoggingKey.TABLES_WITHOUT_DESCRIPTION));
            logResult.add(logCheckResult(Diagnostic.COLUMNS_WITHOUT_DESCRIPTION, Column.class, SimpleLoggingKey.COLUMNS_WITHOUT_DESCRIPTION));
            logResult.add(logCheckResult(Diagnostic.COLUMNS_WITH_JSON_TYPE, Column.class, SimpleLoggingKey.COLUMNS_WITH_JSON_TYPE));
            logResult.add(logCheckResult(Diagnostic.COLUMNS_WITH_SERIAL_TYPES, ColumnWithSerialType.class, SimpleLoggingKey.COLUMNS_WITH_SERIAL_TYPES));
            logResult.add(logCheckResult(Diagnostic.FUNCTIONS_WITHOUT_DESCRIPTION, StoredFunction.class, SimpleLoggingKey.FUNCTIONS_WITHOUT_DESCRIPTION));
            logResult.add(logCheckResult(Diagnostic.INDEXES_WITH_BOOLEAN, IndexWithColumns.class, SimpleLoggingKey.INDEXES_WITH_BOOLEAN));
            logResult.add(logCheckResult(Diagnostic.NOT_VALID_CONSTRAINTS, Constraint.class, SimpleLoggingKey.NOT_VALID_CONSTRAINTS));
            logResult.add(logBtreeIndexesOnArrayColumns(exclusions));
            logResult.add(logCheckResult(Diagnostic.SEQUENCE_OVERFLOW, SequenceState.class, SimpleLoggingKey.SEQUENCE_OVERFLOW));
            logResult.add(logCheckResult(Diagnostic.PRIMARY_KEYS_WITH_SERIAL_TYPES, ColumnWithSerialType.class, SimpleLoggingKey.PRIMARY_KEYS_WITH_SERIAL_TYPES));
            logResult.add(logCheckResult(Diagnostic.DUPLICATED_FOREIGN_KEYS, DuplicatedForeignKeys.class, SimpleLoggingKey.DUPLICATED_FOREIGN_KEYS));
            logResult.add(logCheckResult(Diagnostic.INTERSECTED_FOREIGN_KEYS, DuplicatedForeignKeys.class, SimpleLoggingKey.INTERSECTED_FOREIGN_KEYS));
            logResult.add(logCheckResult(Diagnostic.POSSIBLE_OBJECT_NAME_OVERFLOW, AnyObject.class, SimpleLoggingKey.POSSIBLE_OBJECT_NAME_OVERFLOW));
            logResult.add(logCheckResult(Diagnostic.TABLES_NOT_LINKED_TO_OTHERS, Table.class, SimpleLoggingKey.TABLES_NOT_LINKED_TO_OTHERS));
            logResult.add(logCheckResult(Diagnostic.FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE, ForeignKey.class, SimpleLoggingKey.FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE));
            return logResult;
        } finally {
            databaseChecksHolder.set(null);
            pgContextHolder.set(null);
        }
    }

    protected abstract String writeToLog(@Nonnull LoggingKey key, int value);

    @Nonnull
    private String writeZeroToLog(@Nonnull final LoggingKey key) {
        return writeToLog(key, 0);
    }

    @Nonnull
    private String logDuplicatedIndexes(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.DUPLICATED_INDEXES, DuplicatedIndexes.class),
            SkipIndexesByNamePredicate.of(pgContextHolder.get(), exclusions.getDuplicatedIndexesExclusions()), SimpleLoggingKey.DUPLICATED_INDEXES);
    }

    @Nonnull
    private String logIntersectedIndexes(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.INTERSECTED_INDEXES, DuplicatedIndexes.class),
            SkipIndexesByNamePredicate.of(pgContextHolder.get(), exclusions.getIntersectedIndexesExclusions()), SimpleLoggingKey.INTERSECTED_INDEXES);
    }

    @Nonnull
    private String logUnusedIndexes(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.UNUSED_INDEXES, UnusedIndex.class),
            SkipSmallIndexesPredicate.of(exclusions.getIndexSizeThresholdInBytes())
                .and(SkipIndexesByNamePredicate.of(pgContextHolder.get(), exclusions.getUnusedIndexesExclusions())), SimpleLoggingKey.UNUSED_INDEXES);
    }

    @Nonnull
    private String logTablesWithMissingIndexes(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.TABLES_WITH_MISSING_INDEXES, TableWithMissingIndex.class),
            SkipSmallTablesPredicate.of(exclusions.getTableSizeThresholdInBytes())
                .and(SkipTablesByNamePredicate.of(pgContextHolder.get(), exclusions.getTablesWithMissingIndexesExclusions())), SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES);
    }

    @Nonnull
    private String logTablesWithoutPrimaryKey(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.TABLES_WITHOUT_PRIMARY_KEY, Table.class),
            SkipSmallTablesPredicate.of(exclusions.getTableSizeThresholdInBytes())
                .and(SkipTablesByNamePredicate.of(pgContextHolder.get(), exclusions.getTablesWithoutPrimaryKeyExclusions())), SimpleLoggingKey.TABLES_WITHOUT_PRIMARY_KEY);
    }

    @Nonnull
    private String logIndexesWithNullValues(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.INDEXES_WITH_NULL_VALUES, IndexWithNulls.class),
            SkipIndexesByNamePredicate.of(pgContextHolder.get(), exclusions.getIndexesWithNullValuesExclusions()), SimpleLoggingKey.INDEXES_WITH_NULL_VALUES);
    }

    @Nonnull
    private String logIndexesBloat(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.BLOATED_INDEXES, IndexWithBloat.class),
            SkipBloatUnderThresholdPredicate.of(exclusions.getIndexBloatSizeThresholdInBytes(), exclusions.getIndexBloatPercentageThreshold())
                .and(SkipSmallIndexesPredicate.of(exclusions.getIndexSizeThresholdInBytes())), SimpleLoggingKey.BLOATED_INDEXES);
    }

    @Nonnull
    private String logTablesBloat(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.BLOATED_TABLES, TableWithBloat.class),
            SkipBloatUnderThresholdPredicate.of(exclusions.getTableBloatSizeThresholdInBytes(), exclusions.getTableBloatPercentageThreshold())
                .and(SkipSmallTablesPredicate.of(exclusions.getTableSizeThresholdInBytes())), SimpleLoggingKey.BLOATED_TABLES);
    }

    private String logBtreeIndexesOnArrayColumns(@Nonnull final Exclusions exclusions) {
        return logCheckResult(databaseChecksHolder.get().getCheck(Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS, Index.class),
            SkipIndexesByNamePredicate.of(pgContextHolder.get(), exclusions.getBtreeIndexesOnArrayColumnsExclusions()), SimpleLoggingKey.BTREE_INDEXES_ON_ARRAY_COLUMNS);
    }

    @Nonnull
    private <T extends DbObject> String logCheckResult(@Nonnull final Diagnostic diagnostic,
                                                       @Nonnull final Class<T> type,
                                                       @Nonnull final LoggingKey key) {
        final DatabaseCheckOnCluster<T> check = databaseChecksHolder.get().getCheck(diagnostic, type);
        return logCheckResult(check, c -> true, key);
    }

    @Nonnull
    private <T extends DbObject> String logCheckResult(@Nonnull final DatabaseCheckOnCluster<T> check,
                                                       @Nonnull final Predicate<? super T> exclusionsFilter,
                                                       @Nonnull final LoggingKey key) {
        final List<T> checkResult = check.check(pgContextHolder.get(), exclusionsFilter);
        if (!checkResult.isEmpty()) {
            LOGGER.warn("There are {} in the database {}", key.getDescription(), checkResult);
            return writeToLog(key, checkResult.size());
        }
        return writeZeroToLog(key);
    }
}
