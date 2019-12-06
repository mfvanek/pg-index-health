/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A typical error is when you create a column with an UNIQUE CONSTRAINT and then manually create an unique index on it.
 * See documentation https://www.postgresql.org/docs/10/ddl-constraints.html#DDL-CONSTRAINTS-UNIQUE-CONSTRAINTS.
 */
public class DuplicatedIndexes implements TableAware {

    private final List<IndexWithSize> duplicatedIndexes;
    private final long totalSize;

    private DuplicatedIndexes(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        this.duplicatedIndexes = List.copyOf(Validators.validateThatTableIsTheSame(duplicatedIndexes));
        this.totalSize = duplicatedIndexes.stream()
                .mapToLong(IndexWithSize::getIndexSizeInBytes)
                .sum();
    }

    @Override
    @Nonnull
    public String getTableName() {
        return duplicatedIndexes.get(0).getTableName();
    }

    @Nonnull
    public List<IndexWithSize> getDuplicatedIndexes() {
        return duplicatedIndexes;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public Set<String> getIndexNames() {
        return duplicatedIndexes.stream()
                .map(Index::getIndexName)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return DuplicatedIndexes.class.getSimpleName() + "{" +
                "tableName=\'" + getTableName() + "\'" +
                ", totalSize=" + totalSize +
                ", indexes=" + duplicatedIndexes +
                "}";
    }

    public static DuplicatedIndexes of(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    public static DuplicatedIndexes of(@Nonnull final String tableName, @Nonnull final String duplicatedAsString) {
        Validators.tableNameNotBlank(tableName);
        final var indexesWithNameAndSize = parseAsIndexNameAndSize(
                Validators.notBlank(duplicatedAsString, "duplicatedAsString"));
        final var duplicatedIndexes = indexesWithNameAndSize.stream()
                .map(e -> IndexWithSize.of(tableName, e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new DuplicatedIndexes(duplicatedIndexes);
    }

    private static List<Map.Entry<String, Long>> parseAsIndexNameAndSize(@Nonnull final String duplicatedAsString) {
        final String[] indexes = duplicatedAsString.split("; ");
        return Arrays.stream(indexes)
                .map(s -> s.split(", "))
                .filter(a -> a[0].startsWith("idx=") && a[1].startsWith("size="))
                .map(a -> {
                    final String indexName = a[0].substring("idx=".length());
                    final String sizeAsString = a[1].substring("size=".length());
                    return new AbstractMap.SimpleEntry<>(indexName, Long.parseLong(sizeAsString));
                })
                .collect(Collectors.toList());
    }
}
