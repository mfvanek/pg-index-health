/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.health.DatabaseHealth;
import io.github.mfvanek.pg.model.BloatAware;
import io.github.mfvanek.pg.model.DuplicatedIndexes;
import io.github.mfvanek.pg.model.ForeignKey;
import io.github.mfvanek.pg.model.Index;
import io.github.mfvanek.pg.model.IndexNameAware;
import io.github.mfvanek.pg.model.IndexSizeAware;
import io.github.mfvanek.pg.model.IndexWithBloat;
import io.github.mfvanek.pg.model.IndexWithNulls;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.Table;
import io.github.mfvanek.pg.model.TableNameAware;
import io.github.mfvanek.pg.model.TableSizeAware;
import io.github.mfvanek.pg.model.TableWithBloat;
import io.github.mfvanek.pg.model.TableWithMissingIndex;
import io.github.mfvanek.pg.model.UnusedIndex;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractHealthLogger implements HealthLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHealthLogger.class);

    private final DatabaseHealth databaseHealth;

    @SuppressWarnings("WeakerAccess")
    protected AbstractHealthLogger(@Nonnull final DatabaseHealth databaseHealth) {
        this.databaseHealth = Objects.requireNonNull(databaseHealth);
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
        final List<String> logResult = new ArrayList<>();
        logResult.add(logInvalidIndexes(pgContext));
        logResult.add(logDuplicatedIndexes(exclusions, pgContext));
        logResult.add(logIntersectedIndexes(exclusions, pgContext));
        logResult.add(logUnusedIndexes(exclusions, pgContext));
        logResult.add(logForeignKeysNotCoveredWithIndex(pgContext));
        logResult.add(logTablesWithMissingIndexes(exclusions, pgContext));
        logResult.add(logTablesWithoutPrimaryKey(exclusions, pgContext));
        logResult.add(logIndexesWithNullValues(exclusions, pgContext));
        logResult.add(logIndexesBloat(exclusions, pgContext));
        logResult.add(logTablesBloat(exclusions, pgContext));
        return logResult;
    }

    protected abstract String writeToLog(@Nonnull LoggingKey key, int value);

    @Nonnull
    private String writeZeroToLog(@Nonnull final LoggingKey key) {
        return writeToLog(key, 0);
    }

    @Nonnull
    private String logInvalidIndexes(@Nonnull final PgContext pgContext) {
        final List<Index> invalidIndexes = databaseHealth.getInvalidIndexes(pgContext);
        final LoggingKey key = SimpleLoggingKey.INVALID_INDEXES;
        if (CollectionUtils.isNotEmpty(invalidIndexes)) {
            LOGGER.error("There are invalid indexes in the database {}", invalidIndexes);
            return writeToLog(key, invalidIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logDuplicatedIndexes(@Nonnull final Exclusions exclusions,
                                        @Nonnull final PgContext pgContext) {
        final List<DuplicatedIndexes> rawDuplicatedIndexes = databaseHealth.getDuplicatedIndexes(pgContext);
        final List<DuplicatedIndexes> duplicatedIndexes = applyExclusions(rawDuplicatedIndexes,
                exclusions.getDuplicatedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.DUPLICATED_INDEXES;
        if (CollectionUtils.isNotEmpty(duplicatedIndexes)) {
            LOGGER.warn("There are duplicated indexes in the database {}", duplicatedIndexes);
            return writeToLog(key, duplicatedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIntersectedIndexes(@Nonnull final Exclusions exclusions,
                                         @Nonnull final PgContext pgContext) {
        final List<DuplicatedIndexes> rawIntersectedIndexes = databaseHealth.getIntersectedIndexes(pgContext);
        final List<DuplicatedIndexes> intersectedIndexes = applyExclusions(rawIntersectedIndexes,
                exclusions.getIntersectedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.INTERSECTED_INDEXES;
        if (CollectionUtils.isNotEmpty(intersectedIndexes)) {
            LOGGER.warn("There are intersected indexes in the database {}", intersectedIndexes);
            return writeToLog(key, intersectedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logUnusedIndexes(@Nonnull final Exclusions exclusions,
                                    @Nonnull final PgContext pgContext) {
        final List<UnusedIndex> rawUnusedIndexes = databaseHealth.getUnusedIndexes(pgContext);
        final List<UnusedIndex> filteredUnusedIndexes = applyIndexesExclusions(
                rawUnusedIndexes, exclusions.getUnusedIndexesExclusions());
        final List<UnusedIndex> unusedIndexes = applyIndexSizeExclusions(
                filteredUnusedIndexes, exclusions.getIndexSizeThresholdInBytes());
        final LoggingKey key = SimpleLoggingKey.UNUSED_INDEXES;
        if (CollectionUtils.isNotEmpty(unusedIndexes)) {
            LOGGER.warn("There are unused indexes in the database {}", unusedIndexes);
            return writeToLog(key, unusedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logForeignKeysNotCoveredWithIndex(@Nonnull final PgContext pgContext) {
        final List<ForeignKey> foreignKeys = databaseHealth.getForeignKeysNotCoveredWithIndex(pgContext);
        final LoggingKey key = SimpleLoggingKey.FOREIGN_KEYS;
        if (CollectionUtils.isNotEmpty(foreignKeys)) {
            LOGGER.warn("There are foreign keys without index in the database {}", foreignKeys);
            return writeToLog(key, foreignKeys.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithMissingIndexes(@Nonnull final Exclusions exclusions,
                                               @Nonnull final PgContext pgContext) {
        final List<TableWithMissingIndex> rawTablesWithMissingIndexes = databaseHealth.getTablesWithMissingIndexes(pgContext);
        final List<TableWithMissingIndex> tablesFilteredBySize = applyTableSizeExclusions(
                rawTablesWithMissingIndexes, exclusions.getTableSizeThresholdInBytes());
        final List<TableWithMissingIndex> tablesWithMissingIndexes = applyTablesExclusions(
                tablesFilteredBySize, exclusions.getTablesWithMissingIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES;
        if (CollectionUtils.isNotEmpty(tablesWithMissingIndexes)) {
            LOGGER.warn("There are tables with missing indexes in the database {}", tablesWithMissingIndexes);
            return writeToLog(key, tablesWithMissingIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithoutPrimaryKey(@Nonnull final Exclusions exclusions,
                                              @Nonnull final PgContext pgContext) {
        final List<Table> rawTablesWithoutPrimaryKey = databaseHealth.getTablesWithoutPrimaryKey(pgContext);
        final List<Table> tablesFilteredBySize = applyTableSizeExclusions(
                rawTablesWithoutPrimaryKey, exclusions.getTableSizeThresholdInBytes());
        final List<Table> tablesWithoutPrimaryKey = applyTablesExclusions(
                tablesFilteredBySize, exclusions.getTablesWithoutPrimaryKeyExclusions());
        final LoggingKey key = SimpleLoggingKey.TABLES_WITHOUT_PK;
        if (CollectionUtils.isNotEmpty(tablesWithoutPrimaryKey)) {
            LOGGER.warn("There are tables without primary key in the database {}", tablesWithoutPrimaryKey);
            return writeToLog(key, tablesWithoutPrimaryKey.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIndexesWithNullValues(@Nonnull final Exclusions exclusions,
                                            @Nonnull final PgContext pgContext) {
        final List<IndexWithNulls> rawIndexesWithNullValues = databaseHealth.getIndexesWithNullValues(pgContext);
        final List<IndexWithNulls> indexesWithNullValues = applyIndexesExclusions(rawIndexesWithNullValues,
                exclusions.getIndexesWithNullValuesExclusions());
        final LoggingKey key = SimpleLoggingKey.INDEXES_WITH_NULLS;
        if (CollectionUtils.isNotEmpty(indexesWithNullValues)) {
            LOGGER.warn("There are indexes with null values in the database {}", indexesWithNullValues);
            return writeToLog(key, indexesWithNullValues.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIndexesBloat(@Nonnull final Exclusions exclusions,
                                   @Nonnull final PgContext pgContext) {
        final List<IndexWithBloat> rawIndexesWithBloat = databaseHealth.getIndexesWithBloat(pgContext);
        final List<IndexWithBloat> filteredIndexesWithBloat = applyIndexSizeExclusions(
                rawIndexesWithBloat, exclusions.getIndexSizeThresholdInBytes());
        final List<IndexWithBloat> indexesWithBloat = applyBloatExclusions(filteredIndexesWithBloat,
                exclusions.getIndexBloatSizeThresholdInBytes(), exclusions.getIndexBloatPercentageThreshold());
        final LoggingKey key = SimpleLoggingKey.INDEXES_BLOAT;
        if (CollectionUtils.isNotEmpty(indexesWithBloat)) {
            LOGGER.warn("There are indexes with bloat in the database {}", indexesWithBloat);
            return writeToLog(key, indexesWithBloat.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesBloat(@Nonnull final Exclusions exclusions,
                                  @Nonnull final PgContext pgContext) {
        final List<TableWithBloat> rawTablesWithBloat = databaseHealth.getTablesWithBloat(pgContext);
        final List<TableWithBloat> filteredTablesWithBloat = applyTableSizeExclusions(
                rawTablesWithBloat, exclusions.getTableSizeThresholdInBytes());
        final List<TableWithBloat> tablesWithBloat = applyBloatExclusions(filteredTablesWithBloat,
                exclusions.getTableBloatSizeThresholdInBytes(), exclusions.getTableBloatPercentageThreshold());
        final LoggingKey key = SimpleLoggingKey.TABLES_BLOAT;
        if (CollectionUtils.isNotEmpty(tablesWithBloat)) {
            LOGGER.warn("There are tables with bloat in the database {}", tablesWithBloat);
            return writeToLog(key, tablesWithBloat.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private List<DuplicatedIndexes> applyExclusions(@Nonnull final List<DuplicatedIndexes> rawIndexes,
                                                    @Nonnull final Set<String> indexesExclusions) {
        if (CollectionUtils.isEmpty(rawIndexes) ||
                CollectionUtils.isEmpty(indexesExclusions)) {
            return rawIndexes;
        }

        return rawIndexes.stream()
                .filter(i -> i.getIndexNames().stream()
                        .map(String::toLowerCase)
                        .noneMatch(indexesExclusions::contains))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends IndexNameAware> List<T> applyIndexesExclusions(@Nonnull final List<T> rawRecords,
                                                                             @Nonnull final Set<String> exclusions) {
        if (CollectionUtils.isEmpty(rawRecords) || CollectionUtils.isEmpty(exclusions)) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(i -> !exclusions.contains(i.getIndexName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends IndexSizeAware> List<T> applyIndexSizeExclusions(@Nonnull final List<T> rawRecords,
                                                                               final long threshold) {
        if (CollectionUtils.isEmpty(rawRecords) || threshold <= 0L) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(i -> i.getIndexSizeInBytes() >= threshold)
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends TableNameAware> List<T> applyTablesExclusions(@Nonnull final List<T> rawRecords,
                                                                            @Nonnull final Set<String> exclusions) {
        if (CollectionUtils.isEmpty(rawRecords) || CollectionUtils.isEmpty(exclusions)) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(t -> !exclusions.contains(t.getTableName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends TableSizeAware> List<T> applyTableSizeExclusions(@Nonnull final List<T> rawRecords,
                                                                               final long threshold) {
        if (CollectionUtils.isEmpty(rawRecords) || threshold <= 0L) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(i -> i.getTableSizeInBytes() >= threshold)
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends BloatAware> List<T> applyBloatExclusions(@Nonnull final List<T> rawRecords,
                                                                       final long sizeThreshold,
                                                                       final int percentageThreshold) {
        if (CollectionUtils.isEmpty(rawRecords) || (sizeThreshold <= 0L && percentageThreshold <= 0)) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(r -> r.getBloatSizeInBytes() >= sizeThreshold)
                .filter(r -> r.getBloatPercentage() >= percentageThreshold)
                .collect(Collectors.toList());
    }
}
