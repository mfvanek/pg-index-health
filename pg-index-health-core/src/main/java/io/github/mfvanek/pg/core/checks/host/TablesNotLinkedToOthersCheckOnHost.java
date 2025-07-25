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
import io.github.mfvanek.pg.core.checks.extractors.TableExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.Table;

import java.util.List;

/**
 * Check for tables that are not linked to other tables on a specific host.
 * <p>
 * These are often service tables that are not part of the project, or
 * tables that are no longer in use or were created by mistake, but were not deleted in a timely manner.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.2
 */
public class TablesNotLinkedToOthersCheckOnHost extends AbstractCheckOnHost<Table> {

    public TablesNotLinkedToOthersCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_NOT_LINKED_TO_OTHERS);
    }

    /**
     * Returns tables that are no longer in use or were created by mistake.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of tables that are no longer in use or were created by mistake
     */
    @Override
    protected List<Table> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
