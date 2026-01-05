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
import io.github.mfvanek.pg.model.index.Index;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link Index} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class IndexExtractor implements ResultSetExtractor<Index> {

    /**
     * Represents the column name "index_name" in a ResultSet.
     * Used to extract the "index_name" field from query results when mapping to the {@code IndexWithColumns} model.
     */
    public static final String INDEX_NAME = "index_name";
    /**
     * Represents the column name "index_size" in a ResultSet.
     * Used to extract the "index_size" field from query results when mapping to the {@code IndexWithColumns} model.
     */
    public static final String INDEX_SIZE = "index_size";

    private IndexExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Index extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String indexName = resultSet.getString(INDEX_NAME);
        final long indexSize = resultSet.getLong(INDEX_SIZE);
        return Index.of(tableName, indexName, indexSize);
    }

    /**
     * Creates {@code IndexExtractor} instance.
     *
     * @return {@code IndexExtractor} instance.
     */
    public static ResultSetExtractor<Index> of() {
        return new IndexExtractor();
    }
}
