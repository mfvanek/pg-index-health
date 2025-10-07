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
import io.github.mfvanek.pg.model.table.TableWithBloat;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link TableWithBloat} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class TableWithBloatExtractor implements ResultSetExtractor<TableWithBloat> {

    private TableWithBloatExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableWithBloat extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final long tableSize = resultSet.getLong(TableExtractor.TABLE_SIZE);
        final long bloatSize = resultSet.getLong(IndexWithBloatExtractor.BLOAT_SIZE);
        final double bloatPercentage = resultSet.getDouble(IndexWithBloatExtractor.BLOAT_PERCENTAGE);
        return TableWithBloat.of(tableName, tableSize, bloatSize, bloatPercentage);
    }

    /**
     * Creates {@code TableWithBloatExtractor} instance.
     *
     * @return {@code TableWithBloatExtractor} instance.
     */
    public static ResultSetExtractor<TableWithBloat> of() {
        return new TableWithBloatExtractor();
    }
}
