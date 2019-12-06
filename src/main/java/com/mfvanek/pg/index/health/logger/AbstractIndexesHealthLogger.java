/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndexesHealth;
import com.mfvanek.pg.model.DuplicatedIndexes;
import com.mfvanek.pg.model.IndexAware;
import com.mfvanek.pg.model.TableAware;
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
        logResult.add(logInvalidIndices());
        logResult.add(logDuplicatedIndices());
        logResult.add(logIntersectedIndices());
        logResult.add(logUnusedIndices());
        logResult.add(logForeignKeysNotCoveredWithIndex());
        logResult.add(logTablesWithMissingIndices());
        logResult.add(logTablesWithoutPrimaryKey());
        logResult.add(logIndicesWithNullValues());
        return logResult;
    }

    protected abstract String writeToLog(@Nonnull LoggingKey key, int value);

    @Nonnull
    private String writeZeroToLog(@Nonnull final LoggingKey key) {
        return writeToLog(key, 0);
    }

    @Nonnull
    private String logInvalidIndices() {
        final var invalidIndices = indexesHealth.getInvalidIndexes();
        final LoggingKey key = SimpleLoggingKey.INVALID_INDEXES;
        if (CollectionUtils.isNotEmpty(invalidIndices)) {
            LOGGER.error("There are invalid indices in the database {}", invalidIndices);
            return writeToLog(key, invalidIndices.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logDuplicatedIndices() {
        final var rawDuplicatedIndices = indexesHealth.getDuplicatedIndexes();
        final var duplicatedIndices = applyExclusions(rawDuplicatedIndices,
                exclusions.getDuplicatedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.DUPLICATED_INDEXES;
        if (CollectionUtils.isNotEmpty(duplicatedIndices)) {
            LOGGER.warn("There are duplicated indices in the database {}", duplicatedIndices);
            return writeToLog(key, duplicatedIndices.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logIntersectedIndices() {
        final var rawIntersectedIndices = indexesHealth.getIntersectedIndexes();
        final var intersectedIndices = applyExclusions(rawIntersectedIndices,
                exclusions.getIntersectedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.INTERSECTED_INDEXES;
        if (CollectionUtils.isNotEmpty(intersectedIndices)) {
            LOGGER.warn("There are intersected indices in the database {}", intersectedIndices);
            return writeToLog(key, intersectedIndices.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private String logUnusedIndices() {
        final var rawUnusedIndices = indexesHealth.getUnusedIndexes();
        final var unusedIndices = applyIndicesExclusions(rawUnusedIndices, exclusions.getUnusedIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.UNUSED_INDEXES;
        if (CollectionUtils.isNotEmpty(unusedIndices)) {
            LOGGER.warn("There are unused indices in the database {}", unusedIndices);
            return writeToLog(key, unusedIndices.size());
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
    private String logTablesWithMissingIndices() {
        final var rawTablesWithMissingIndices = indexesHealth.getTablesWithMissingIndexes();
        final var tablesWithMissingIndices = applyTablesExclusions(rawTablesWithMissingIndices,
                exclusions.getTablesWithMissingIndexesExclusions());
        final LoggingKey key = SimpleLoggingKey.TABLES_WITH_MISSING_INDEXES;
        if (CollectionUtils.isNotEmpty(tablesWithMissingIndices)) {
            LOGGER.warn("There are tables with missing indices in the database {}", tablesWithMissingIndices);
            return writeToLog(key, tablesWithMissingIndices.size());
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
    private String logIndicesWithNullValues() {
        final var rawIndicesWithNullValues = indexesHealth.getIndexesWithNullValues();
        final var indicesWithNullValues = applyIndicesExclusions(rawIndicesWithNullValues,
                exclusions.getIndexesWithNullValuesExclusions());
        final LoggingKey key = SimpleLoggingKey.INDEXES_WITH_NULLS;
        if (CollectionUtils.isNotEmpty(indicesWithNullValues)) {
            LOGGER.warn("There are indices with null values in the database {}", indicesWithNullValues);
            return writeToLog(key, indicesWithNullValues.size());
        }
        return writeZeroToLog(key);
    }

    @Nonnull
    private List<DuplicatedIndexes> applyExclusions(@Nonnull final List<DuplicatedIndexes> rawIndices,
                                                    @Nonnull final Set<String> indicesExclusions) {
        if (CollectionUtils.isEmpty(rawIndices) ||
                CollectionUtils.isEmpty(indicesExclusions)) {
            return rawIndices;
        }

        return rawIndices.stream()
                .filter(i -> i.getIndexNames().stream()
                        .map(String::toLowerCase)
                        .noneMatch(indicesExclusions::contains))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends IndexAware> List<T> applyIndicesExclusions(@Nonnull final List<T> rawRecords,
                                                                         @Nonnull final Set<String> exclusions) {
        if (CollectionUtils.isEmpty(rawRecords) ||
                CollectionUtils.isEmpty(exclusions)) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(i -> !exclusions.contains(i.getIndexName().toLowerCase()))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static <T extends TableAware> List<T> applyTablesExclusions(@Nonnull final List<T> rawRecords,
                                                                        @Nonnull final Set<String> exclusions) {
        if (CollectionUtils.isEmpty(rawRecords) ||
                CollectionUtils.isEmpty(exclusions)) {
            return rawRecords;
        }

        return rawRecords.stream()
                .filter(t -> !exclusions.contains(t.getTableName().toLowerCase()))
                .collect(Collectors.toList());
    }
}
