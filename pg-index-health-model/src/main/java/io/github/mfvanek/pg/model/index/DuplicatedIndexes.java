/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.index.utils.DuplicatedIndexesParser;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of duplicated indexes in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 */
@Immutable
public class DuplicatedIndexes implements DbObject, TableNameAware {

    private static final Comparator<IndexWithSize> INDEX_WITH_SIZE_COMPARATOR =
        Comparator.comparing(IndexWithSize::getTableName)
            .thenComparing(IndexWithSize::getIndexName)
            .thenComparing(IndexWithSize::getIndexSizeInBytes);

    private final List<IndexWithSize> indexes;
    private final long totalSize;
    private final List<String> indexesNames;

    private DuplicatedIndexes(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        final List<IndexWithSize> defensiveCopy = List.copyOf(Objects.requireNonNull(duplicatedIndexes, "duplicatedIndexes cannot be null"));
        validateThatTableIsTheSame(defensiveCopy);
        this.indexes = defensiveCopy.stream()
            .sorted(INDEX_WITH_SIZE_COMPARATOR)
            .collect(Collectors.toUnmodifiableList());
        this.totalSize = this.indexes.stream()
            .mapToLong(IndexWithSize::getIndexSizeInBytes)
            .sum();
        this.indexesNames = this.indexes.stream()
            .map(Index::getIndexName)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public final String getName() {
        return String.join(",", indexesNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return indexes.get(0).getTableName();
    }

    /**
     * Gets raw list of duplicated indexes.
     *
     * @return list of duplicated indexes
     */
    @Nonnull
    public List<IndexWithSize> getDuplicatedIndexes() {
        return indexes;
    }

    /**
     * Gets total size in bytes of all duplicated indexes.
     *
     * @return size in bytes
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Gets names of all duplicated indexes.
     *
     * @return sorted list
     */
    public List<String> getIndexNames() {
        return indexesNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof DuplicatedIndexes)) {
            return false;
        }

        final DuplicatedIndexes that = (DuplicatedIndexes) other;
        return Objects.equals(indexes, that.indexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(indexes);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return DuplicatedIndexes.class.getSimpleName() + '{' +
            "tableName='" + getTableName() + '\'' +
            ", totalSize=" + totalSize +
            ", indexes=" + indexes +
            '}';
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from given list of indexes.
     *
     * @param duplicatedIndexes list of duplicated indexes; should be non-null.
     * @return {@code DuplicatedIndexes}
     */
    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from given table name and raw string queried from database.
     *
     * @param tableName          table name; should be non-blank.
     * @param duplicatedAsString duplicated indexes as a raw string; should be non-blank.
     * @return {@code DuplicatedIndexes}
     */
    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final String tableName, @Nonnull final String duplicatedAsString) {
        Validators.tableNameNotBlank(tableName);
        final List<Map.Entry<String, Long>> indexesWithNameAndSize = DuplicatedIndexesParser.parseAsIndexNameAndSize(
            Validators.notBlank(duplicatedAsString, "duplicatedAsString"));
        final List<IndexWithSize> duplicatedIndexes = indexesWithNameAndSize.stream()
            .map(e -> IndexWithSize.of(tableName, e.getKey(), e.getValue()))
            .collect(Collectors.toUnmodifiableList());
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from given indexes.
     *
     * @param firstIndex   first index; should be non-null.
     * @param secondIndex  second index; should be non-null.
     * @param otherIndexes other indexes
     * @return {@code DuplicatedIndexes}
     */
    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final IndexWithSize firstIndex,
                                       @Nonnull final IndexWithSize secondIndex,
                                       @Nonnull final IndexWithSize... otherIndexes) {
        Objects.requireNonNull(firstIndex, "firstIndex cannot be null");
        Objects.requireNonNull(secondIndex, "secondIndex cannot be null");
        if (Stream.of(otherIndexes).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("otherIndexes cannot contain nulls");
        }
        final Stream<IndexWithSize> basePart = Stream.of(firstIndex, secondIndex);
        return new DuplicatedIndexes(Stream.concat(basePart, Stream.of(otherIndexes))
            .collect(Collectors.toUnmodifiableList()));
    }

    private static void validateThatTableIsTheSame(@Nonnull final List<? extends TableNameAware> duplicatedIndexes) {
        final String tableName = validateThatContainsAtLeastTwoRows(duplicatedIndexes).get(0).getTableName();
        Validators.validateThatTableIsTheSame(tableName, duplicatedIndexes);
    }

    @Nonnull
    private static <T> List<T> validateThatContainsAtLeastTwoRows(@Nonnull final List<T> duplicatedIndexes) {
        final int size = Objects.requireNonNull(duplicatedIndexes).size();
        if (0 == size) {
            throw new IllegalArgumentException("duplicatedIndexes cannot be empty");
        }
        if (size < 2) {
            throw new IllegalArgumentException("duplicatedIndexes should contains at least two rows");
        }
        return duplicatedIndexes;
    }
}
