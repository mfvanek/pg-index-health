/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.index.utils.DuplicatedIndexesParser;
import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable representation of duplicated indexes in a database.
 *
 * @author Ivan Vakhrushev
 * @see TableNameAware
 */
public final class DuplicatedIndexes implements DbObject, TableNameAware, IndexesAware {

    /**
     * Represents the field name used to store information about indexes.
     */
    public static final String INDEXES_FIELD = "indexes";
    /**
     * Represents the field name used to store the total size of duplicated indexes.
     */
    public static final String TOTAL_SIZE_FIELD = "totalSize";

    private static final Comparator<Index> INDEX_WITH_SIZE_COMPARATOR =
        Comparator.comparing(Index::getTableName)
            .thenComparing(Index::getIndexName)
            .thenComparing(Index::getIndexSizeInBytes);

    private final List<Index> indexes;
    private final long totalSize;
    private final List<String> indexesNames;

    private DuplicatedIndexes(final Collection<Index> indexes) {
        final List<Index> defensiveCopy = List.copyOf(Objects.requireNonNull(indexes, INDEXES_FIELD + " cannot be null"));
        Validators.validateThatTableIsTheSame(defensiveCopy);
        this.indexes = defensiveCopy.stream()
            .sorted(INDEX_WITH_SIZE_COMPARATOR)
            .toList();
        this.totalSize = this.indexes.stream()
            .mapToLong(Index::getIndexSizeInBytes)
            .sum();
        this.indexesNames = this.indexes.stream()
            .map(Index::getIndexName)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return String.join(",", indexesNames);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.INDEX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTableName() {
        return indexes.get(0).getTableName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return indexes;
    }

    /**
     * Retrieves the total size in bytes of all duplicated indexes.
     *
     * @return size in bytes
     */
    public long getTotalSize() {
        return totalSize;
    }

    /**
     * Retrieves names of all duplicated indexes.
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
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final DuplicatedIndexes that)) {
            return false;
        }

        return Objects.equals(indexes, that.indexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(indexes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return DuplicatedIndexes.class.getSimpleName() + '{' +
            TABLE_NAME_FIELD + "='" + getTableName() + '\'' +
            ", " + TOTAL_SIZE_FIELD + '=' + totalSize +
            ", " + INDEXES_FIELD + '=' + indexes +
            '}';
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from a given list of indexes.
     *
     * @param duplicatedIndexes list of duplicated indexes; should be non-null.
     * @return {@code DuplicatedIndexes}
     */
    public static DuplicatedIndexes of(final Collection<Index> duplicatedIndexes) {
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from given table name and raw string queried from a database.
     *
     * @param tableName          table name; should be non-blank.
     * @param duplicatedAsString duplicated indexes as a raw string; should be non-blank.
     * @return {@code DuplicatedIndexes}
     */
    public static DuplicatedIndexes of(final String tableName, final String duplicatedAsString) {
        Validators.tableNameNotBlank(tableName);
        final List<Map.Entry<String, Long>> indexesWithNameAndSize = DuplicatedIndexesParser.parseAsIndexNameAndSize(
            Validators.notBlank(duplicatedAsString, "duplicatedAsString"));
        final List<Index> duplicatedIndexes = indexesWithNameAndSize.stream()
            .map(e -> Index.of(tableName, e.getKey(), e.getValue()))
            .toList();
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    /**
     * Constructs an {@code DuplicatedIndexes} object from given indexes.
     *
     * @param firstIndex   the first index; should be non-null.
     * @param secondIndex  the second index; should be non-null.
     * @param otherIndexes other indexes.
     * @return {@code DuplicatedIndexes}
     */
    public static DuplicatedIndexes of(final Index firstIndex,
                                       final Index secondIndex,
                                       final Index... otherIndexes) {
        return new DuplicatedIndexes(DuplicatedIndexesParser.combine(firstIndex, secondIndex, otherIndexes));
    }
}
