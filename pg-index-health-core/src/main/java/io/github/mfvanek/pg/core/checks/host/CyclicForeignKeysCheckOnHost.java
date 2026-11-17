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
import io.github.mfvanek.pg.core.checks.extractors.ForeignKeyCycleExtractor;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;

/**
 * Check for cyclic foreign keys (chains of foreign keys that lead from a table back to itself through one or more other tables) on a specific host.
 * <p>
 * Any cycle is reported, even if its foreign keys are deferrable or built on nullable columns.
 * Self-referenced foreign keys and cycles that pass through a table in another schema are not reported.
 * </p>
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.schemacrawler.com/lint.html">SchemaCrawler Lint: LinterTableCycles</a>
 * @since 0.50.0
 */
public class CyclicForeignKeysCheckOnHost extends AbstractCheckOnHost<ForeignKeyCycle> {

    /**
     * Constructs a new instance of {@code CyclicForeignKeysCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public CyclicForeignKeysCheckOnHost(final PgConnection pgConnection) {
        super(ForeignKeyCycle.class, pgConnection, Diagnostic.CYCLIC_FOREIGN_KEYS, ForeignKeyCycleExtractor.of());
    }
}
