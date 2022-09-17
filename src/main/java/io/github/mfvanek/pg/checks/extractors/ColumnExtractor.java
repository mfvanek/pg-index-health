/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.table.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

/**
 * A mapper from raw data to {@link Column} model.
 *
 * @author Ivan Vahrushev
 * @since 0.6.1
 */
public class ColumnExtractor implements ResultSetExtractor<Column> {

    private ColumnExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Column extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString("table_name");
        final String columnName = resultSet.getString("column_name");
        final boolean columnNotNull = resultSet.getBoolean("column_not_null");
        if (columnNotNull) {
            return Column.ofNotNull(tableName, columnName);
        }
        return Column.ofNullable(tableName, columnName);
    }

    @Nonnull
    public static ResultSetExtractor<Column> of() {
        return new ColumnExtractor();
    }
}
