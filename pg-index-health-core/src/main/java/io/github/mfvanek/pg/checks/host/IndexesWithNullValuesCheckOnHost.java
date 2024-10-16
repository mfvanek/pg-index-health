/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithNulls;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for indexes with null values on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class IndexesWithNullValuesCheckOnHost extends AbstractCheckOnHost<IndexWithNulls> {

    public IndexesWithNullValuesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(IndexWithNulls.class, pgConnection, Diagnostic.INDEXES_WITH_NULL_VALUES);
    }

    /**
     * Returns indexes that contain null values in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of indexes with null values
     */
    @Nonnull
    @Override
    protected List<IndexWithNulls> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            final long indexSize = rs.getLong(INDEX_SIZE);
            final String nullableField = rs.getString("nullable_fields");
            return IndexWithNulls.of(tableName, indexName, indexSize, nullableField);
        });
    }
}
