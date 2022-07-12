/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.UnusedIndex;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for unused indexes on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class UnusedIndexesCheckOnHost extends AbstractCheckOnHost<UnusedIndex> {

    public UnusedIndexesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(UnusedIndex.class, pgConnection, Diagnostic.UNUSED_INDEXES);
    }

    /**
     * Returns unused indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of unused indexes
     */
    @Nonnull
    @Override
    public List<UnusedIndex> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            final long indexSize = rs.getLong(INDEX_SIZE);
            final long indexScans = rs.getLong("index_scans");
            return UnusedIndex.of(tableName, indexName, indexSize, indexScans);
        });
    }
}
