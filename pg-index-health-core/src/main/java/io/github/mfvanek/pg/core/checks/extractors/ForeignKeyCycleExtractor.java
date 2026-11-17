/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.core.utils.ColumnsDataParser;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A mapper from raw data to {@link ForeignKeyCycle} model.
 * <p>
 * A cycle is returned as three parallel arrays with one element per foreign key column.
 * Consecutive elements with the same table and constraint name belong to the same (multicolumn) foreign key.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see ForeignKeyExtractor
 * @since 0.50.0
 */
public final class ForeignKeyCycleExtractor implements ResultSetExtractor<ForeignKeyCycle> {

    /**
     * Represents the column name "table_names" in a ResultSet.
     */
    public static final String TABLE_NAMES = "table_names";
    /**
     * Represents the column name "constraint_names" in a ResultSet.
     */
    public static final String CONSTRAINT_NAMES = "constraint_names";

    private ForeignKeyCycleExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ForeignKeyCycle extractData(final ResultSet resultSet) throws SQLException {
        final String[] tableNames = getArray(resultSet, TABLE_NAMES);
        final String[] constraintNames = getArray(resultSet, CONSTRAINT_NAMES);
        final String[] rawColumns = getArray(resultSet, ColumnsAware.COLUMNS_FIELD);
        if (tableNames.length != constraintNames.length || tableNames.length != rawColumns.length) {
            throw new IllegalArgumentException("Arrays " + TABLE_NAMES + ", " + CONSTRAINT_NAMES + " and " + ColumnsAware.COLUMNS_FIELD + " must have the same length");
        }
        final List<ForeignKey> foreignKeys = new ArrayList<>();
        int start = 0;
        for (int i = 1; i <= tableNames.length; ++i) {
            // Constraint names are unique only within a table, so both names are needed to find the boundaries of a foreign key
            if (i == tableNames.length || !tableNames[i].equals(tableNames[start]) || !constraintNames[i].equals(constraintNames[start])) {
                final String tableName = tableNames[start];
                foreignKeys.add(ForeignKey.of(tableName, constraintNames[start],
                    ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex(tableName, Arrays.copyOfRange(rawColumns, start, i))));
                start = i;
            }
        }
        return ForeignKeyCycle.of(foreignKeys);
    }

    private static String[] getArray(final ResultSet resultSet, final String columnName) throws SQLException {
        return (String[]) resultSet.getArray(columnName).getArray();
    }

    /**
     * Creates {@code ForeignKeyCycleExtractor} instance.
     *
     * @return {@code ForeignKeyCycleExtractor} instance
     */
    public static ResultSetExtractor<ForeignKeyCycle> of() {
        return new ForeignKeyCycleExtractor();
    }
}
