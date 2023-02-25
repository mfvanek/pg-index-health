/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

public class ColumnWithSerialTypeExtractor implements ResultSetExtractor<ColumnWithSerialType> {

    private final ResultSetExtractor<Column> columnExtractor;

    private ColumnWithSerialTypeExtractor() {
        this.columnExtractor = ColumnExtractor.of();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ColumnWithSerialType extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final Column column = columnExtractor.extractData(resultSet);
        final String columnType = resultSet.getString("column_type");
        final String sequenceName = resultSet.getString("sequence_name");
        return ColumnWithSerialType.of(column, SerialType.valueFrom(columnType), sequenceName);
    }

    @Nonnull
    public static ResultSetExtractor<ColumnWithSerialType> of() {
        return new ColumnWithSerialTypeExtractor();
    }
}
