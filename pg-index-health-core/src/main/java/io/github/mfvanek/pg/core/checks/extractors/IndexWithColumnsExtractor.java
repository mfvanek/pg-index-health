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
import io.github.mfvanek.pg.core.utils.ColumnsDataParser;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A mapper from raw data with multiple columns to {@link IndexWithColumns} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public final class IndexWithColumnsExtractor implements ResultSetExtractor<IndexWithColumns> {

    private IndexWithColumnsExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithColumns extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String indexName = resultSet.getString(IndexExtractor.INDEX_NAME);
        final long indexSize = resultSet.getLong(IndexExtractor.INDEX_SIZE);
        final Array columnsArray = resultSet.getArray(ColumnsAware.COLUMNS_FIELD);
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex(tableName, rawColumns);
        return IndexWithColumns.ofColumns(tableName, indexName, indexSize, columns);
    }

    /**
     * Creates {@code IndexWithColumnsExtractor} instance.
     *
     * @return {@code IndexWithColumnsExtractor} instance
     */
    public static ResultSetExtractor<IndexWithColumns> of() {
        return new IndexWithColumnsExtractor();
    }
}
