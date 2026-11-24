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
import io.github.mfvanek.pg.model.column.Column;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link Column} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
public final class ColumnExtractor implements ResultSetExtractor<Column> {

    /**
     * The name of the result set column that contains the column type.
     *
     * @since 0.60.0
     */
    public static final String COLUMN_TYPE = "column_type";

    private ColumnExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Column extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String columnName = resultSet.getString("column_name");
        final boolean columnNotNull = resultSet.getBoolean("column_not_null");
        final String columnType = resultSet.getString(COLUMN_TYPE);
        return Column.of(tableName, columnName, columnNotNull, columnType);
    }

    /**
     * Creates {@code ColumnExtractor} instance.
     *
     * @return {@code ColumnExtractor} instance
     */
    public static ResultSetExtractor<Column> of() {
        return new ColumnExtractor();
    }
}
