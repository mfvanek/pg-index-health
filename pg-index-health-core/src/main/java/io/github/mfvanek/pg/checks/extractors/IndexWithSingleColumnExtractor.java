/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link IndexWithColumns} model.
 *
 * @author Ivan Vahrushev
 * @since 0.11.0
 */
public class IndexWithSingleColumnExtractor implements ResultSetExtractor<IndexWithColumns> {

    public static final String INDEX_NAME = "index_name";
    public static final String INDEX_SIZE = "index_size";

    private final ResultSetExtractor<Column> columnExtractor;

    private IndexWithSingleColumnExtractor() {
        this.columnExtractor = ColumnExtractor.of();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public IndexWithColumns extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String indexName = resultSet.getString(INDEX_NAME);
        final long indexSize = resultSet.getLong(INDEX_SIZE);
        final Column column = columnExtractor.extractData(resultSet);
        return IndexWithColumns.ofSingle(tableName, indexName, indexSize, column);
    }

    /**
     * Creates {@code IndexWithSingleColumnExtractor} instance.
     *
     * @return {@code IndexWithSingleColumnExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<IndexWithColumns> of() {
        return new IndexWithSingleColumnExtractor();
    }
}
