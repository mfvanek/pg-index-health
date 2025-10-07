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
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link IndexWithColumns} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class IndexWithNullableColumnExtractor implements ResultSetExtractor<IndexWithColumns> {

    private IndexWithNullableColumnExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithColumns extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String indexName = resultSet.getString(IndexExtractor.INDEX_NAME);
        final long indexSize = resultSet.getLong(IndexExtractor.INDEX_SIZE);
        final String nullableField = resultSet.getString("nullable_fields");
        final Column nullableColumn = Column.ofNullable(tableName, nullableField);
        return IndexWithColumns.ofSingle(tableName, indexName, indexSize, nullableColumn);
    }

    /**
     * Creates {@code IndexWithNullableColumnExtractor} instance.
     *
     * @return {@code IndexWithNullableColumnExtractor} instance.
     */
    public static ResultSetExtractor<IndexWithColumns> of() {
        return new IndexWithNullableColumnExtractor();
    }
}
