package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DuplicatedIndexes implements TableAware {

    private final List<IndexWithSize> duplicatedIndexes;
    private final long totalSize;

    public DuplicatedIndexes(@Nonnull List<IndexWithSize> duplicatedIndexes) {
        this.duplicatedIndexes = Objects.requireNonNull(duplicatedIndexes);
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
}
