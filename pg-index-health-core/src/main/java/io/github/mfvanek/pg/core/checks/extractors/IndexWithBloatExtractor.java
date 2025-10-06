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
import io.github.mfvanek.pg.model.index.IndexWithBloat;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link IndexWithBloat} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class IndexWithBloatExtractor implements ResultSetExtractor<IndexWithBloat> {

    /**
     * Represents the column name used to retrieve bloat size from the database result set.
     */
    public static final String BLOAT_SIZE = "bloat_size";
    /**
     * Represents the column name used to retrieve bloat percentage from the database result set.
     */
    public static final String BLOAT_PERCENTAGE = "bloat_percentage";

    private IndexWithBloatExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IndexWithBloat extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String indexName = resultSet.getString(IndexWithSingleColumnExtractor.INDEX_NAME);
        final long indexSize = resultSet.getLong(IndexWithSingleColumnExtractor.INDEX_SIZE);
        final long bloatSize = resultSet.getLong(BLOAT_SIZE);
        final double bloatPercentage = resultSet.getDouble(BLOAT_PERCENTAGE);
        return IndexWithBloat.of(tableName, indexName, indexSize, bloatSize, bloatPercentage);
    }

    /**
     * Creates {@code IndexWithBloatExtractor} instance.
     *
     * @return {@code IndexWithBloatExtractor} instance.
     */
    public static ResultSetExtractor<IndexWithBloat> of() {
        return new IndexWithBloatExtractor();
    }
}
