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
import java.util.stream.Collectors;

public class DuplicatedIndexes implements TableAware {

    private final List<IndexWithSize> duplicatedIndexes;
    private final long totalSize;

    private DuplicatedIndexes(@Nonnull final List<IndexWithSize> duplicatedIndexes) {
        this.duplicatedIndexes = Validators.validateThatTableIsTheSame(duplicatedIndexes);
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
    public List<String> getIndexNames() {
        return duplicatedIndexes.stream()
                .map(Index::getIndexName)
                .collect(Collectors.toList());
    }

    public long getTotalSize() {
        return totalSize;
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
