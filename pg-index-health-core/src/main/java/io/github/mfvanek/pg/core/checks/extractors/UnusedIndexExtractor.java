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
import io.github.mfvanek.pg.model.index.UnusedIndex;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link UnusedIndex} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class UnusedIndexExtractor implements ResultSetExtractor<UnusedIndex> {

    private UnusedIndexExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UnusedIndex extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String indexName = resultSet.getString(IndexExtractor.INDEX_NAME);
        final long indexSize = resultSet.getLong(IndexExtractor.INDEX_SIZE);
        final long indexScans = resultSet.getLong("index_scans");
        return UnusedIndex.of(tableName, indexName, indexSize, indexScans);
    }

    /**
     * Creates {@code UnusedIndexExtractor} instance.
     *
     * @return {@code UnusedIndexExtractor} instance.
     */
    public static ResultSetExtractor<UnusedIndex> of() {
        return new UnusedIndexExtractor();
    }
}
