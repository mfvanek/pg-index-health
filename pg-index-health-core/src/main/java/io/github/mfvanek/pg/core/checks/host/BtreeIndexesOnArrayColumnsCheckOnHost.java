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
import io.github.mfvanek.pg.core.checks.extractors.IndexWithSingleColumnExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;

/**
 * Check for B-tree indexes on array columns on a specific host.
 * <p>
 * GIN-index should be used instead for such columns.
 *
 * @author Vadim Khizhin
 * @since 0.11.0
 */
public class BtreeIndexesOnArrayColumnsCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code BtreeIndexesOnArrayColumnsCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public BtreeIndexesOnArrayColumnsCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.BTREE_INDEXES_ON_ARRAY_COLUMNS);
    }

    /**
     * Returns B-tree indexes on array columns in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of B-tree indexes on array columns
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithSingleColumnExtractor.of());
    }
}
