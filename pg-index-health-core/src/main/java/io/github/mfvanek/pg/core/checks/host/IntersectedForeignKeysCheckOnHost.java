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
import io.github.mfvanek.pg.core.checks.extractors.DuplicatedForeignKeysExtractor;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for intersected (partially identical) foreign keys on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see DuplicatedForeignKeysCheckOnHost
 * @since 0.13.1
 */
public class IntersectedForeignKeysCheckOnHost extends AbstractCheckOnHost<DuplicatedForeignKeys> {

    /**
     * Creates a new {@code IntersectedForeignKeysCheckOnHost} object.
     *
     * @param pgConnection connection to the PostgreSQL database, must not be null
     */
    public IntersectedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        super(DuplicatedForeignKeys.class, pgConnection, Diagnostic.INTERSECTED_FOREIGN_KEYS);
    }

    /**
     * Returns intersected (partially identical) foreign keys in the specified schema (except completely identical).
     *
     * @param pgContext check's context with the specified schema
     * @return list of intersected foreign keys
     * @see DuplicatedForeignKeysCheckOnHost
     */
    @Override
    protected List<DuplicatedForeignKeys> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, DuplicatedForeignKeysExtractor.of("intersected"));
    }
}
