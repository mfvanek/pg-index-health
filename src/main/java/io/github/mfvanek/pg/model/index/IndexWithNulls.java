/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

public final class IndexWithNulls extends IndexWithSize {

    private final String nullableField;

    private IndexWithNulls(@Nonnull final String tableName,
                           @Nonnull final String indexName,
                           final long indexSizeInBytes,
                           @Nonnull final String nullableField) {
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
                ", nullableField='" + nullableField + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static IndexWithNulls of(@Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    @Nonnull final String nullableField) {
        return new IndexWithNulls(tableName, indexName, indexSizeInBytes, nullableField);
    }
}
