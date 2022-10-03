/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.column.Column;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class ColumnsInForeignKeyParser {

    private ColumnsInForeignKeyParser() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static List<Column> parseRawColumnData(@Nonnull final String tableName, @Nonnull final String... rawColumns) {
        Validators.tableNameNotBlank(tableName);
        Objects.requireNonNull(rawColumns, "rawColumns cannot be null");
        if (rawColumns.length == 0) {
            throw new IllegalArgumentException("Columns array cannot be empty");
        }
        return Arrays.stream(rawColumns)
                .map(c -> toColumn(tableName, c))
                .collect(Collectors.toList());
    }

    @Nonnull
    private static Column toColumn(@Nonnull final String tableName, @Nonnull final String rawColumnInfo) {
        final String[] columnInfo = rawColumnInfo.split(", ");
        if (columnInfo.length != 2) {
            throw new IllegalArgumentException("Cannot parse column info from " + rawColumnInfo);
        }
        final boolean notNullColumn = Boolean.parseBoolean(columnInfo[1]);
        if (notNullColumn) {
            return Column.ofNotNull(tableName, columnInfo[0]);
        }
        return Column.ofNullable(tableName, columnInfo[0]);
    }
}
