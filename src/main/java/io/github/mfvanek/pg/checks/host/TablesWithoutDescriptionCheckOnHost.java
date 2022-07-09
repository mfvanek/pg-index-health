/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.AbstractCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.Table;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for tables without description on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public class TablesWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<Table> {

    public TablesWithoutDescriptionCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.TABLES_WITHOUT_DESCRIPTION);
    }

    /**
     * Returns tables without description (comment) in the specified schema.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">SQL Commands - COMMENT</a>
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables without description
     */
    @Nonnull
    @Override
    public List<Table> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            return Table.of(tableName, tableSize);
        });
    }
}
