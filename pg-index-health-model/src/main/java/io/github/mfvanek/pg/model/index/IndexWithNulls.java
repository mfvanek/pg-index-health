/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class IndexWithNulls extends IndexWithSize {

    private final Column nullableColumn;

    private IndexWithNulls(@Nonnull final String tableName,
                           @Nonnull final String indexName,
                           final long indexSizeInBytes,
                           @Nonnull final Column nullableColumn) {
        super(tableName, indexName, indexSizeInBytes);
        Objects.requireNonNull(nullableColumn, "nullableColumn cannot be null");
        Validators.validateThatTableIsTheSame(tableName, List.of(nullableColumn));
        this.nullableColumn = nullableColumn;
    }

    @Nonnull
    public Column getNullableColumn() {
        return nullableColumn;
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return IndexWithNulls.class.getSimpleName() + '{' +
                innerToString() +
                ", nullableColumn=" + nullableColumn + '}';
    }

    public static IndexWithNulls of(@Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    @Nonnull final String nullableColumnName) {
        return new IndexWithNulls(tableName, indexName, indexSizeInBytes, Column.ofNullable(tableName, nullableColumnName));
    }
}
