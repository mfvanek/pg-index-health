/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;

/**
 * Check for indexes with null values on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class IndexesWithNullValuesCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    public IndexesWithNullValuesCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.INDEXES_WITH_NULL_VALUES);
    }

    /**
     * Returns indexes that contain null values in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of indexes with null values
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            final long indexSize = rs.getLong(INDEX_SIZE);
            final String nullableField = rs.getString("nullable_fields");
            final Column nullableColumn = Column.ofNullable(tableName, nullableField);
            return IndexWithColumns.ofSingle(tableName, indexName, indexSize, nullableColumn);
        });
    }
}
