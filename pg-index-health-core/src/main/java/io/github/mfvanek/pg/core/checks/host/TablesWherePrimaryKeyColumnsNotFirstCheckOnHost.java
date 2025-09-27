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
 * Check for tables where the primary key column is not the first column in the table on a specific host.
 * <p>
 * Putting the primary key as the first column improves readability, consistency, and expectations, especially in teams or large schemas.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TablesWherePrimaryKeyColumnsNotFirstCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code TablesWherePrimaryKeyColumnsNotFirstCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWherePrimaryKeyColumnsNotFirstCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WHERE_PRIMARY_KEY_COLUMNS_NOT_FIRST);
    }

    /**
     * Returns tables where the primary key columns are not first in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of tables where the primary key columns are not first
     */
    @Override
    protected List<Table> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
