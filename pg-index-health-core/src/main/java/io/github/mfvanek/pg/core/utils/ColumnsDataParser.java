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

/**
 * Utility class for parsing raw column information and generating
 * {@link Column} objects associated with a specific database table.
 */
public final class ColumnsDataParser {

    private ColumnsDataParser() {
        throw new UnsupportedOperationException();
    }

    /**
     * Parses raw column definitions and generates a list of {@code Column} objects
     * related to a specific table, typically used within contexts such as foreign keys
     * or indexes. The method enforces that at least one column must be provided.
     *
     * @param tableName  the name of the table to which the columns belong; must be non-blank.
     * @param rawColumns the raw column definitions; each definition is expected to contain the column name followed by a comma and a boolean indicating nullability.
     * @return a list of {@code Column} objects representing the parsed columns with their respective table association and nullability.
     */
    public static List<Column> parseRawColumnsInForeignKeyOrIndex(final String tableName, final String... rawColumns) {
        return parseRawColumnsData(tableName, true, rawColumns);
    }

    /**
     * Parses raw column definitions and generates a list of {@code Column} objects
     * associated with a specific table. This method allows for zero or more columns
     * to be provided and determines nullability based on the input data.
     *
     * @param tableName  the name of the table to which the columns belong; must be non-blank.
     * @param rawColumns the raw column definitions as strings; each definition is expected to specify the column name followed by a comma and a boolean indicating nullability.
     * @return a list of {@code Column} objects representing the parsed column information with their associated table and nullability.
     */
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
