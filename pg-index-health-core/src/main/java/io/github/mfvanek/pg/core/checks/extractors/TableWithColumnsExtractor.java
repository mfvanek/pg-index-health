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
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithColumns;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A mapper from raw data to {@link TableWithColumns} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public final class TableWithColumnsExtractor implements ResultSetExtractor<TableWithColumns> {

    private TableWithColumnsExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TableWithColumns extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final long tableSize = resultSet.getLong(TableExtractor.TABLE_SIZE);
        final Array columnsArray = resultSet.getArray(ColumnsAware.COLUMNS_FIELD);
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsDataParser.parseRawColumnsInTable(tableName, rawColumns);
        return TableWithColumns.of(Table.of(tableName, tableSize), columns);
    }

    /**
     * Creates {@code TableWithColumnsExtractor} instance.
     *
     * @return {@code TableWithColumnsExtractor} instance
     */
    public static ResultSetExtractor<TableWithColumns> of() {
        return new TableWithColumnsExtractor();
    }
}
