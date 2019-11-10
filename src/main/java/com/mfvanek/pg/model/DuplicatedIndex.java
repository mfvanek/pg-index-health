package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class DuplicatedIndex {

    private final String tableName;
    private final long totalSize;
    private final List<String> indexNames;

    public DuplicatedIndex(String tableName, long totalSize, List<String> indexNames) {
        this.tableName = Objects.requireNonNull(tableName);
        this.totalSize = totalSize;
        this.indexNames = Objects.requireNonNull(indexNames);
    }

    @Nonnull
    public String getTableName() {
        return tableName;
    }

    @Nonnull
    public List<String> getIndexNames() {
        return indexNames;
    }

    @Override
    public String toString() {
        return DuplicatedIndex.class.getSimpleName() + "{" +
                "tableName=" + tableName +
                ", totalSize=" + totalSize +
                ", indexNames=" + indexNames +
                "}";
    }
}
