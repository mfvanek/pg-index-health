/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndexesHealth;
import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.IndexNameAware;
import com.mfvanek.pg.model.IndexSizeAware;
import com.mfvanek.pg.model.TableNameAware;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractIndexesHealthLogger implements IndexesHealthLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIndexesHealthLogger.class);

    private final IndexesHealth indexesHealth;
    private final Exclusions exclusions;

    protected AbstractIndexesHealthLogger(@Nonnull final IndexesHealth indexesHealth,
                                          @Nonnull final Exclusions exclusions) {
        this.indexesHealth = Objects.requireNonNull(indexesHealth);
        this.exclusions = Objects.requireNonNull(exclusions);
    }

    @Override
    @Nonnull
    public final List<String> logAll() {
        final List<String> logResult = new ArrayList<>();
        logResult.add(logInvalidIndexes());
        logResult.add(logDuplicatedIndexes());
        logResult.add(logIntersectedIndexes());
        logResult.add(logUnusedIndexes());
        logResult.add(logForeignKeysNotCoveredWithIndex());
        logResult.add(logTablesWithMissingIndexes());
        logResult.add(logTablesWithoutPrimaryKey());
        logResult.add(logIndexesWithNullValues());
        return logResult;
    }

    protected abstract String writeToLog(@Nonnull LoggingKey key, int value);

    @Nonnull
    private String writeZeroToLog(@Nonnull final LoggingKey key) {
        return writeToLog(key, 0);
    }

    @Nonnull
    private String logInvalidIndexes() {
        final var invalidIndexes = indexesHealth.getInvalidIndexes();
        final LoggingKey key = SimpleLoggingKey.INVALID_INDEXES;
        if (CollectionUtils.isNotEmpty(invalidIndexes)) {
            LOGGER.error("There are invalid indexes in the database {}", invalidIndexes);
            return writeToLog(key, invalidIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logDuplicatedIndexes() {
        final var rawDuplicatedIndexes = indexesHealth.getDuplicatedIndexes();
        final var duplicatedIndexes = applyExclusions(rawDuplicatedIndexes,
                exclusions.getDuplicatedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.DUPLICATED_INDEXES;
        if (CollectionUtils.isNotEmpty(duplicatedIndexes)) {
            LOGGER.warn("There are duplicated indexes in the database {}", duplicatedIndexes);
            return writeToLog(key, duplicatedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIntersectedIndexes() {
        final var rawIntersectedIndexes = indexesHealth.getIntersectedIndexes();
        final var intersectedIndexes = applyExclusions(rawIntersectedIndexes,
                exclusions.getIntersectedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.INTERSECTED_INDEXES;
        if (CollectionUtils.isNotEmpty(intersectedIndexes)) {
            LOGGER.warn("There are intersected indexes in the database {}", intersectedIndexes);
            return writeToLog(key, intersectedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logUnusedIndexes() {
        final var rawUnusedIndexes = indexesHealth.getUnusedIndexes();
        final var filteredUnusedIndexes = applyIndexesExclusions(rawUnusedIndexes, exclusions.getUnusedIndexesExclusions());
        final var unusedIndexes = applyIndexSizeExclusions(filteredUnusedIndexes, exclusions.getIndexSizeThreshold());
        final LoggingKey key = SimpleLoggingKey.UNUSED_INDEXES;
        if (CollectionUtils.isNotEmpty(unusedIndexes)) {
            LOGGER.warn("There are unused indexes in the database {}", unusedIndexes);
            return writeToLog(key, unusedIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logForeignKeysNotCoveredWithIndex() {
        final var foreignKeys = indexesHealth.getForeignKeysNotCoveredWithIndex();
        final LoggingKey key = SimpleLoggingKey.FOREIGN_KEYS;
        if (CollectionUtils.isNotEmpty(foreignKeys)) {
            LOGGER.warn("There are foreign keys without index in the database {}", foreignKeys);
            return writeToLog(key, foreignKeys.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithMissingIndexes() {
        final var rawTablesWithMissingIndexes = indexesHealth.getTablesWithMissingIndexes();
        final var tablesWithMissingIndexes = applyTablesExclusions(rawTablesWithMissingIndexes,
                exclusions.getTablesWithMissingIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES;
        if (CollectionUtils.isNotEmpty(tablesWithMissingIndexes)) {
            LOGGER.warn("There are tables with missing indexes in the database {}", tablesWithMissingIndexes);
            return writeToLog(key, tablesWithMissingIndexes.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logTablesWithoutPrimaryKey() {
        final var rawTablesWithoutPrimaryKey = indexesHealth.getTablesWithoutPrimaryKey();
        final var tablesWithoutPrimaryKey = applyTablesExclusions(rawTablesWithoutPrimaryKey,
                exclusions.getTablesWithoutPrimaryKeyExclusions());
        final LoggingKey key = SimpleLoggingKey.TABLES_WITHOUT_PK;
        if (CollectionUtils.isNotEmpty(tablesWithoutPrimaryKey)) {
            LOGGER.warn("There are tables without primary key in the database {}", tablesWithoutPrimaryKey);
            return writeToLog(key, tablesWithoutPrimaryKey.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIndexesWithNullValues() {
        final var rawIndexesWithNullValues = indexesHealth.getIndexesWithNullValues();
        final var indexesWithNullValues = applyIndexesExclusions(rawIndexesWithNullValues,
                exclusions.getIndexesWithNullValuesExclusions());
        final LoggingKey key = SimpleLoggingKey.INDEXES_WITH_NULLS;
        if (CollectionUtils.isNotEmpty(indexesWithNullValues)) {
            LOGGER.warn("There are indexes with null values in the database {}", indexesWithNullValues);
            return writeToLog(key, indexesWithNullValues.size());
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
}
