/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for invalid (broken) indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class InvalidIndexesCheckOnHost extends AbstractCheckOnHost<Index> {

    public InvalidIndexesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Index.class, pgConnection, Diagnostic.INVALID_INDEXES);
    }

    /**
     * Returns invalid (broken) indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Nonnull
    @Override
    protected List<Index> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String indexName = rs.getString(INDEX_NAME);
            return Index.of(tableName, indexName);
        });
    }
}
