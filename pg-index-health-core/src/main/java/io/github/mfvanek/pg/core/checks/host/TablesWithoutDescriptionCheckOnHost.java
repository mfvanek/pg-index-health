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
 * Check for tables without description on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class TablesWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<Table> {

    public TablesWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WITHOUT_DESCRIPTION);
    }

    /**
     * Returns tables without description (comment) in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables without description
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">SQL Commands - COMMENT</a>
     */
    @Override
    protected List<Table> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
