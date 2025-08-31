/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.column.ColumnWithType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link ColumnWithType} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public final class ColumnWithTypeExtractor implements ResultSetExtractor<ColumnWithType> {

    /**
     * Column name constant representing the type of column in the database.
     */
    public static final String COLUMN_TYPE = "column_type";

    private final ResultSetExtractor<Column> columnExtractor;

    private ColumnWithTypeExtractor() {
        this.columnExtractor = ColumnExtractor.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithType extractData(final ResultSet resultSet) throws SQLException {
        final Column column = columnExtractor.extractData(resultSet);
        final String columnType = resultSet.getString(COLUMN_TYPE);
        return ColumnWithType.of(column, columnType);
    }

    /**
     * Creates {@code ColumnWithTypeExtractor} instance.
     *
     * @return {@code ColumnWithTypeExtractor} instance
     */
    public static ResultSetExtractor<ColumnWithType> of() {
        return new ColumnWithTypeExtractor();
    }
}
