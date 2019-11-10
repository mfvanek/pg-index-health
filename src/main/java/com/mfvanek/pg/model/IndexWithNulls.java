package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

public class IndexWithNulls extends Index {

    private final long indexSizeInBytes;
    private final String nullableField;

    public IndexWithNulls(@Nonnull String tableName,
                          @Nonnull String indexName,
                          long indexSizeInBytes,
                          @Nonnull String nullableField) {
        super(tableName, indexName);
        this.indexSizeInBytes = indexSizeInBytes;
        this.nullableField = Objects.requireNonNull(nullableField);
    }

    @Nonnull
    public String getNullableField() {
        return nullableField;
    }

    public long getIndexSizeInBytes() {
        return indexSizeInBytes;
    }

    @Override
    public String toString() {
        return IndexWithNulls.class.getSimpleName() + "{" +
                innerToString() +
                ", indexSizeInBytes=" + indexSizeInBytes +
                ", nullableField=" + nullableField +
                "}";
    }
}
