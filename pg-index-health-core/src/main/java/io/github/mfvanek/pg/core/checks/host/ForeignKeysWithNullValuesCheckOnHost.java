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
import io.github.mfvanek.pg.core.checks.extractors.ForeignKeyExtractor;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

/**
 * Check for composite (multi-column) foreign keys with nullable columns
 * that are not defined with MATCH FULL on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class ForeignKeysWithNullValuesCheckOnHost extends AbstractCheckOnHost<ForeignKey> {

    /**
     * Constructs a new instance of {@code ForeignKeysWithNullValuesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ForeignKeysWithNullValuesCheckOnHost(final PgConnection pgConnection) {
        super(ForeignKey.class, pgConnection, Diagnostic.FOREIGN_KEYS_WITH_NULL_VALUES, ForeignKeyExtractor.ofDefault());
    }
}
