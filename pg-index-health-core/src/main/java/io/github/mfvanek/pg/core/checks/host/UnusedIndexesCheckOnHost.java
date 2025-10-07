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
import io.github.mfvanek.pg.core.checks.extractors.UnusedIndexExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.UnusedIndex;

import java.util.List;

/**
 * Check for unused indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class UnusedIndexesCheckOnHost extends AbstractCheckOnHost<UnusedIndex> {

    /**
     * Constructs a new instance of {@code UnusedIndexesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public UnusedIndexesCheckOnHost(final PgConnection pgConnection) {
        super(UnusedIndex.class, pgConnection, Diagnostic.UNUSED_INDEXES);
    }

    /**
     * Returns unused indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of unused indexes
     */
    @Override
    protected List<UnusedIndex> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, UnusedIndexExtractor.of());
    }
}
