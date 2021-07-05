/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.table.TableNameAware;
import io.github.mfvanek.pg.utils.DuplicatedIndexesParser;
import io.github.mfvanek.pg.utils.Validators;

import java.util.Collections;
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
public class DuplicatedIndexes implements TableNameAware {

    private static final Comparator<IndexWithSize> INDEX_WITH_SIZE_COMPARATOR =
            Comparator.comparing(IndexWithSize::getTableName)
                    .thenComparing(IndexWithSize::getIndexName)
                    .thenComparing(IndexWithSize::getIndexSizeInBytes);

    private final List<IndexWithSize> duplicatedIndexes;
    private final long totalSize;
    private final List<String> indexesNames;

    private DuplicatedIndexes(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        this.duplicatedIndexes = Collections.unmodifiableList(
                Validators.validateThatTableIsTheSame(duplicatedIndexes).stream()
                        .sorted(INDEX_WITH_SIZE_COMPARATOR)
                        .collect(Collectors.toList()));
        this.totalSize = duplicatedIndexes.stream()
                .mapToLong(IndexWithSize::getIndexSizeInBytes)
                .sum();
        this.indexesNames = Collections.unmodifiableList(
                this.duplicatedIndexes.stream()
                        .map(Index::getIndexName)
                        .collect(Collectors.toList()));
    }

    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final String tableName, @Nonnull final String duplicatedAsString) {
        Validators.tableNameNotBlank(tableName);
        final List<Map.Entry<String, Long>> indexesWithNameAndSize = DuplicatedIndexesParser.parseAsIndexNameAndSize(
                Validators.notBlank(duplicatedAsString, "duplicatedAsString"));
        final List<IndexWithSize> duplicatedIndexes = indexesWithNameAndSize.stream()
                .map(e -> IndexWithSize.of(tableName, e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    @Nonnull
    public static DuplicatedIndexes of(@Nonnull final IndexWithSize firstIndex,
                                       @Nonnull final IndexWithSize secondIndex,
                                       @Nonnull final IndexWithSize... otherIndexes) {
        final Stream<IndexWithSize> basePart = Stream.of(Objects.requireNonNull(firstIndex, "firstIndex"),
                Objects.requireNonNull(secondIndex, "secondIndex"));
        if (Stream.of(otherIndexes).anyMatch(Objects::isNull)) {
            throw new NullPointerException("otherIndexes");
        }
        return new DuplicatedIndexes(Stream.concat(basePart, Stream.of(otherIndexes))
                .collect(Collectors.toList()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String getTableName() {
        return duplicatedIndexes.get(0).getTableName();
    }

    /**
     * Gets raw list of duplicated indexes.
     *
     * @return list of duplicated indexes
     */
    @Nonnull
    public List<IndexWithSize> getDuplicatedIndexes() {
        return duplicatedIndexes;
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof DuplicatedIndexes)) {
            return false;
        }

        final DuplicatedIndexes that = (DuplicatedIndexes) o;
        return Objects.equals(duplicatedIndexes, that.duplicatedIndexes);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(duplicatedIndexes);
    }

    @Override
    public String toString() {
        return DuplicatedIndexes.class.getSimpleName() + '{' +
                "tableName='" + getTableName() + '\'' +
                ", totalSize=" + totalSize +
                ", indexes=" + duplicatedIndexes +
                '}';
    }
}
