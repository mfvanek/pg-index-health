/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.TableWithBloat;

import java.util.List;

/**
 * Check for tables bloat on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class TablesWithBloatCheckOnHost extends AbstractCheckOnHost<TableWithBloat> {

    public TablesWithBloatCheckOnHost(final PgConnection pgConnection) {
        super(TableWithBloat.class, pgConnection, Diagnostic.BLOATED_TABLES);
    }

    /**
     * Returns tables that are bloated in the specified schema.
     * <p>
     * Note: The database user on whose behalf this method will be executed
     * have to have read permissions for the corresponding tables.
     * </p>
     *
     * @param pgContext check's context with the specified schema
     * @return list of bloated tables
     */
    @Override
    protected List<TableWithBloat> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            final long bloatSize = rs.getLong(BLOAT_SIZE);
            final double bloatPercentage = rs.getDouble(BLOAT_PERCENTAGE);
            return TableWithBloat.of(tableName, tableSize, bloatSize, bloatPercentage);
        });
    }
}
