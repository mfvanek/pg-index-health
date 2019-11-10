package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

public final class IndexWithNulls extends IndexWithSize {

    private final String nullableField;

    private IndexWithNulls(@Nonnull String tableName,
                           @Nonnull String indexName,
                           long indexSizeInBytes,
                           @Nonnull String nullableField) {
        super(tableName, indexName, indexSizeInBytes);
        this.nullableField = Objects.requireNonNull(nullableField);
    }

    @Nonnull
    public String getNullableField() {
        return nullableField;
    }

    @Override
    public String toString() {
        return IndexWithNulls.class.getSimpleName() + "{" +
                innerToString() +
                ", nullableField=" + nullableField +
                "}";
    }

    public static IndexWithNulls of(@Nonnull String tableName,
                                    @Nonnull String indexName,
                                    long indexSizeInBytes,
                                    @Nonnull String nullableField) {
        return new IndexWithNulls(tableName, indexName, indexSizeInBytes, nullableField);
    }
}
