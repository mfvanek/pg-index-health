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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.Table;

import java.util.List;

/**
 * Check for tables that have all columns besides the primary key that are nullable on a specific host.
 * <p>
 * Such tables may contain no useful data and could indicate a schema design smell.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWhereAllColumnsNullableExceptPrimaryKeyCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WHERE_ALL_COLUMNS_NULLABLE_EXCEPT_PK);
    }

    /**
     * Returns tables that have all columns besides the primary key that are nullable in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of tables that have all columns besides the primary key that are nullable
     */
    @Override
    protected List<Table> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, TableExtractor.of());
    }
}
