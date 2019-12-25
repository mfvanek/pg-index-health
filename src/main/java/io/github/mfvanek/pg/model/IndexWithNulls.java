/*
 * Copyright (c) 2019. Ivan Vakhrushev.
 * https://github.com/mfvanek
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in Postgresql databases.
 */

package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public final class IndexWithNulls extends IndexWithSize {

    private final String nullableField;

    private IndexWithNulls(@Nonnull String tableName,
                           @Nonnull String indexName,
                           long indexSizeInBytes,
                           @Nonnull String nullableField) {
        super(tableName, indexName, indexSizeInBytes);
        this.nullableField = Validators.notBlank(nullableField, "nullableField");
    }

    @Nonnull
    public String getNullableField() {
        return nullableField;
    }

    @Override
    public String toString() {
        return IndexWithNulls.class.getSimpleName() + '{' +
                innerToString() +
                ", nullableField=\'" + nullableField + "\'" +
                '}';
    }

    public static IndexWithNulls of(@Nonnull String tableName,
                                    @Nonnull String indexName,
                                    long indexSizeInBytes,
                                    @Nonnull String nullableField) {
        return new IndexWithNulls(tableName, indexName, indexSizeInBytes, nullableField);
    }
}
