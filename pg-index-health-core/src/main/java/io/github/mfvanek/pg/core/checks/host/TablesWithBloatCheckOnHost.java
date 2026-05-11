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
import io.github.mfvanek.pg.core.checks.extractors.TableWithBloatExtractor;
import io.github.mfvanek.pg.model.table.TableWithBloat;

/**
 * Check for tables bloat on a specific host.
 * <p>
 * Note: The database user on whose behalf this check will be executed
 * has to have read permissions for the corresponding tables.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class TablesWithBloatCheckOnHost extends AbstractCheckOnHost<TableWithBloat> {

    /**
     * Constructs a new instance of {@code TablesWithBloatCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWithBloatCheckOnHost(final PgConnection pgConnection) {
        super(TableWithBloat.class, pgConnection, Diagnostic.BLOATED_TABLES, TableWithBloatExtractor.of());
    }
}
