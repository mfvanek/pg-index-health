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
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class ColumnWithSerialTypeExtractor implements ResultSetExtractor<ColumnWithSerialType> {

    private final ResultSetExtractor<Column> columnExtractor;

    private ColumnWithSerialTypeExtractor() {
        this.columnExtractor = ColumnExtractor.of();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ColumnWithSerialType extractData(final ResultSet resultSet) throws SQLException {
        final Column column = columnExtractor.extractData(resultSet);
        final String columnType = resultSet.getString("column_type");
        final String sequenceName = resultSet.getString("sequence_name");
        return ColumnWithSerialType.of(column, SerialType.valueFrom(columnType), sequenceName);
    }

    /**
     * Creates {@code ColumnWithSerialTypeExtractor} instance.
     *
     * @return {@code ColumnWithSerialTypeExtractor} instance
     */
    public static ResultSetExtractor<ColumnWithSerialType> of() {
        return new ColumnWithSerialTypeExtractor();
    }
}
