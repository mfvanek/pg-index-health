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
 * Check for tables with inheritance on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.1
 */
public class TablesWithInheritanceCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code TablesWithInheritanceCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWithInheritanceCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WITH_INHERITANCE);
    }

    /**
     * Returns tables that use inheritance in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables with inheritance
     * @see <a href="https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_table_inheritance">Don't use table inheritance</a>
     */
    @Override
    protected List<Table> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
