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
import io.github.mfvanek.pg.core.checks.extractors.DuplicatedIndexesExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;

import java.util.List;

/**
 * Check for intersected (partially identical) indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class IntersectedIndexesCheckOnHost extends AbstractCheckOnHost<DuplicatedIndexes> {

    /**
     * Constructs a new instance of {@code IntersectedIndexesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public IntersectedIndexesCheckOnHost(final PgConnection pgConnection) {
        super(DuplicatedIndexes.class, pgConnection, Diagnostic.INTERSECTED_INDEXES);
    }

    /**
     * Returns intersected (partially identical) indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of intersected indexes
     */
    @Override
    protected List<DuplicatedIndexes> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, DuplicatedIndexesExtractor.of("intersected"));
    }
}
