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
import io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;

/**
 * Check for indexes that contain boolean values on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
public class IndexesWithBooleanCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    public IndexesWithBooleanCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.INDEXES_WITH_BOOLEAN);
    }

    /**
     * Returns indexes that contain boolean values in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of indexes that contain boolean values
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithSingleColumnExtractor.of());
    }
}
