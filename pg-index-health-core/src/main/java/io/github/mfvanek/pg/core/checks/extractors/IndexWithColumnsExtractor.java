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
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor.INDEX_NAME;
import static io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor.INDEX_SIZE;
import static io.github.mfvanek.pg.core.checks.extractors.TableExtractor.TABLE_NAME;

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
        final String tableName = resultSet.getString(TABLE_NAME);
        final String indexName = resultSet.getString(INDEX_NAME);
        final long indexSize = resultSet.getLong(INDEX_SIZE);
        final Array columnsArray = resultSet.getArray("columns");
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
