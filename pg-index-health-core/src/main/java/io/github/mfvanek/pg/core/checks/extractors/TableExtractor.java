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
import io.github.mfvanek.pg.model.table.Table;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link Table} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.1
 */
public final class TableExtractor implements ResultSetExtractor<Table> {

    public static final String TABLE_NAME = "table_name";
    public static final String TABLE_SIZE = "table_size";

    private TableExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Table extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final long tableSize = resultSet.getLong(TABLE_SIZE);
        return Table.of(tableName, tableSize);
    }

    /**
     * Creates {@code TableExtractor} instance.
     *
     * @return {@code TableExtractor} instance
     */
    public static ResultSetExtractor<Table> of() {
        return new TableExtractor();
    }
}
