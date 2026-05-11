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
import io.github.mfvanek.pg.core.checks.extractors.TableExtractor;
import io.github.mfvanek.pg.model.table.Table;

/**
 * Check for tables without description on a specific host.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">SQL Commands - COMMENT</a>
 * @since 0.6.0
 */
public class TablesWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code TablesWithoutDescriptionCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WITHOUT_DESCRIPTION, TableExtractor.of());
    }
}
