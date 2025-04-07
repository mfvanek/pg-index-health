/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class ColumnsDataParser {

    private ColumnsDataParser() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public static List<Column> parseRawColumnInForeignKey(@Nonnull final String tableName, @Nonnull final String... rawColumns) {
        return parseRawColumnData(tableName, true, rawColumns);
    }

    @Nonnull
    public static List<Column> parseRawColumnInTable(@Nonnull final String tableName, @Nonnull final String... rawColumns) {
        return parseRawColumnData(tableName, false, rawColumns);
    }

    @Nonnull
    private static List<Column> parseRawColumnData(@Nonnull final String tableName,
                                                   final boolean cannotHaveZeroColumns,
                                                   @Nonnull final String... rawColumns) {
        Validators.tableNameNotBlank(tableName);
        Objects.requireNonNull(rawColumns, "rawColumns cannot be null");
        if (cannotHaveZeroColumns && rawColumns.length == 0) {
            throw new IllegalArgumentException("Columns array cannot be empty");
        }
        return Arrays.stream(rawColumns)
            .map(c -> toColumn(tableName, c))
            .collect(Collectors.toUnmodifiableList());
    }

    @Nonnull
    private static Column toColumn(@Nonnull final String tableName, @Nonnull final String rawColumnInfo) {
        final String[] columnInfo = rawColumnInfo.split(",");
        if (columnInfo.length != 2) {
            throw new IllegalArgumentException("Cannot parse column info from " + rawColumnInfo);
        }
        final boolean notNullColumn = Boolean.parseBoolean(columnInfo[1].trim());
        if (notNullColumn) {
            return Column.ofNotNull(tableName, columnInfo[0].trim());
        }
        return Column.ofNullable(tableName, columnInfo[0].trim());
    }
}
