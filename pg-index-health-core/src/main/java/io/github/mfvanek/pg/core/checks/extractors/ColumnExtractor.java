/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link Column} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
public final class ColumnExtractor implements ResultSetExtractor<Column> {

    private ColumnExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public Column extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String columnName = resultSet.getString("column_name");
        final boolean columnNotNull = resultSet.getBoolean("column_not_null");
        if (columnNotNull) {
            return Column.ofNotNull(tableName, columnName);
        }
        return Column.ofNullable(tableName, columnName);
    }

    /**
     * Creates {@code ColumnExtractor} instance.
     *
     * @return {@code ColumnExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<Column> of() {
        return new ColumnExtractor();
    }
}
