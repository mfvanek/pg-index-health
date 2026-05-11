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
 * Check for tables that are not linked to other tables on a specific host.
 * <p>
 * These are often service tables that are not part of the project, or
 * tables that are no longer in use or were created by mistake but were not deleted in a timely manner.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 */
public class TablesNotLinkedToOthersCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code TablesNotLinkedToOthersCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesNotLinkedToOthersCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_NOT_LINKED_TO_OTHERS, TableExtractor.of());
    }
}
