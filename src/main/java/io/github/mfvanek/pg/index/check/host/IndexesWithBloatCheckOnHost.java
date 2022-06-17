/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.check.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithBloat;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for indexes bloat on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class IndexesWithBloatCheckOnHost extends AbstractCheckOnHost<IndexWithBloat> {

    public IndexesWithBloatCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(IndexWithBloat.class, pgConnection, Diagnostic.BLOATED_INDEXES);
    }

    /**
     * Returns indexes that are bloated in the specified schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContext check's context with the specified schema
     * @return list of bloated indexes
     */
    @Nonnull
    @Override
    public List<IndexWithBloat> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            final long indexSize = rs.getLong(INDEX_SIZE);
            final long bloatSize = rs.getLong(BLOAT_SIZE);
            final int bloatPercentage = rs.getInt(BLOAT_PERCENTAGE);
            return IndexWithBloat.of(tableName, indexName, indexSize, bloatSize, bloatPercentage);
        });
    }
}
