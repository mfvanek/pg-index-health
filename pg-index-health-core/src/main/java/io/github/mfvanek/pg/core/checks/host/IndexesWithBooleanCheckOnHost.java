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
import io.github.mfvanek.pg.core.checks.extractors.IndexWithColumnsExtractor;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

/**
 * Check for indexes that contain boolean values on a specific host.
 * <p>
 * Returns one row per index with all its columns (including {@code INCLUDE} columns) in index order.
 * Expressions are not included in the columns.
 *
 * @author Ivan Vakhrushev
 * @since 0.11.0
 */
public class IndexesWithBooleanCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code IndexesWithBooleanCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public IndexesWithBooleanCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.INDEXES_WITH_BOOLEAN, IndexWithColumnsExtractor.of());
    }
}
