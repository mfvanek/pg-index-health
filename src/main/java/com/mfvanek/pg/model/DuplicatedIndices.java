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

public class DuplicatedIndices implements TableAware {

    private final List<IndexWithSize> duplicatedIndices;
    private final long totalSize;

    private DuplicatedIndices(@Nonnull final List<IndexWithSize> duplicatedIndices) {
        this.duplicatedIndices = List.copyOf(Validators.validateThatTableIsTheSame(duplicatedIndices));
        this.totalSize = duplicatedIndices.stream()
                .mapToLong(IndexWithSize::getIndexSizeInBytes)
                .sum();
    }

    @Override
    @Nonnull
    public String getTableName() {
        return duplicatedIndices.get(0).getTableName();
    }

    @Nonnull
    public List<IndexWithSize> getDuplicatedIndices() {
        return duplicatedIndices;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public Set<String> getIndexNames() {
        return duplicatedIndices.stream()
                .map(Index::getIndexName)
                .collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return DuplicatedIndices.class.getSimpleName() + "{" +
                "tableName=\'" + getTableName() + "\'" +
                ", totalSize=" + totalSize +
                ", indices=" + duplicatedIndices +
                "}";
    }

    public static DuplicatedIndices of(@Nonnull final List<IndexWithSize> duplicatedIndices) {
        return new DuplicatedIndices(duplicatedIndices);
    }

    public static DuplicatedIndices of(@Nonnull final String tableName, @Nonnull final String duplicatedAsString) {
        Validators.tableNameNotBlank(tableName);
        final var indicesWithNameAndSize = parseAsIndexNameAndSize(
                Validators.notBlank(duplicatedAsString, "duplicatedAsString"));
        final var duplicatedIndices = indicesWithNameAndSize.stream()
                .map(e -> IndexWithSize.of(tableName, e.getKey(), e.getValue()))
                .collect(Collectors.toList());
        return new DuplicatedIndices(duplicatedIndices);
    }

    private static List<Map.Entry<String, Long>> parseAsIndexNameAndSize(@Nonnull final String duplicatedAsString) {
        final String[] indices = duplicatedAsString.split("; ");
        return Arrays.stream(indices)
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
