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
import io.github.mfvanek.pg.core.checks.extractors.DuplicatedForeignKeysExtractor;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;

/**
 * Check for intersected (partially identical) foreign keys on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see DuplicatedForeignKeysCheckOnHost
 * @since 0.13.1
 */
public class IntersectedForeignKeysCheckOnHost extends AbstractCheckOnHost<DuplicatedForeignKeys> {

    /**
     * Constructs a new instance of {@code IntersectedForeignKeysCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public IntersectedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        super(DuplicatedForeignKeys.class, pgConnection, Diagnostic.INTERSECTED_FOREIGN_KEYS, DuplicatedForeignKeysExtractor.of("intersected"));
    }
}
