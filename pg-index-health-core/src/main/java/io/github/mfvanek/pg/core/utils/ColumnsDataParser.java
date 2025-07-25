/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ColumnsDataParser {

    private ColumnsDataParser() {
        throw new UnsupportedOperationException();
    }

    public static List<Column> parseRawColumnsInForeignKeyOrIndex(final String tableName, final String... rawColumns) {
        return parseRawColumnsData(tableName, true, rawColumns);
    }

    public static List<Column> parseRawColumnsInTable(final String tableName, final String... rawColumns) {
        return parseRawColumnsData(tableName, false, rawColumns);
    }

    private static List<Column> parseRawColumnsData(final String tableName,
                                                    final boolean cannotHaveZeroColumns,
                                                    final String... rawColumns) {
        Validators.tableNameNotBlank(tableName);
        Objects.requireNonNull(rawColumns, "rawColumns cannot be null");
        if (cannotHaveZeroColumns && rawColumns.length == 0) {
            throw new IllegalArgumentException("Columns array cannot be empty");
        }
        return Arrays.stream(rawColumns)
            .map(c -> toColumn(tableName, c))
            .toList();
    }

    private static Column toColumn(final String tableName, final String rawColumnInfo) {
        final int lastCommaPosition = rawColumnInfo.lastIndexOf(',');
        if (lastCommaPosition == -1) {
            throw new IllegalArgumentException("Cannot parse column info from " + rawColumnInfo);
        }
        final String nullability = rawColumnInfo.substring(lastCommaPosition + 1).trim();
        final boolean notNullColumn = Boolean.parseBoolean(nullability);
        final String columnName = rawColumnInfo.substring(0, lastCommaPosition).trim();
        if (notNullColumn) {
            return Column.ofNotNull(tableName, columnName);
        }
        return Column.ofNullable(tableName, columnName);
    }
}
