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

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * Represents database index with information about size and nullable columns.
 *
 * @author Ivan Vahrushev
 * @since 0.0.1
 */
@Immutable
public final class IndexWithNulls extends IndexWithColumns {

    private IndexWithNulls(@Nonnull final String tableName,
                           @Nonnull final String indexName,
                           final long indexSizeInBytes,
                           @Nonnull final Column nullableColumn) {
        super(tableName, indexName, indexSizeInBytes, List.of(Objects.requireNonNull(nullableColumn, "nullableColumn cannot be null")));
    }

    /**
     * Gets nullable column in index.
     *
     * @return nullable column
     */
    @Nonnull
    public Column getNullableColumn() {
        return getColumns().get(0);
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public String toString() {
        return IndexWithNulls.class.getSimpleName() + '{' + innerToString() + '}';
    }

    /**
     * Constructs an {@code IndexWithNulls} object.
     *
     * @param tableName          table name; should be non-blank.
     * @param indexName          index name; should be non-blank.
     * @param indexSizeInBytes   index size in bytes; should be positive or zero.
     * @param nullableColumnName nullable column in this index.
     * @return {@code IndexWithNulls}
     */
    @Nonnull
    public static IndexWithNulls of(@Nonnull final String tableName,
                                    @Nonnull final String indexName,
                                    final long indexSizeInBytes,
                                    @Nonnull final String nullableColumnName) {
        return new IndexWithNulls(tableName, indexName, indexSizeInBytes, Column.ofNullable(tableName, nullableColumnName));
    }
}
