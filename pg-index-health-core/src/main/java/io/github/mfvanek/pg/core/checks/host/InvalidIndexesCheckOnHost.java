/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.core.checks.extractors.IndexExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.Index;

import java.util.List;

/**
 * Check for invalid (broken) indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class InvalidIndexesCheckOnHost extends AbstractCheckOnHost<Index> {

    /**
     * Constructs a new instance of {@code InvalidIndexesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public InvalidIndexesCheckOnHost(final PgConnection pgConnection) {
        super(Index.class, pgConnection, Diagnostic.INVALID_INDEXES);
    }

    /**
     * Returns invalid (broken) indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of invalid indexes
     * @see Index
     */
    @Override
    protected List<Index> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexExtractor.of());
    }
}
