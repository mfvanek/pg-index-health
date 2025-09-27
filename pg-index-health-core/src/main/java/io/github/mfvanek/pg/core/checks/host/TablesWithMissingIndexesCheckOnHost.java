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
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.util.List;

/**
 * Check for tables with potentially missing indexes on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class TablesWithMissingIndexesCheckOnHost extends AbstractCheckOnHost<TableWithMissingIndex> {

    /**
     * Constructs a new instance of {@code TablesWithMissingIndexesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWithMissingIndexesCheckOnHost(final PgConnection pgConnection) {
        super(TableWithMissingIndex.class, pgConnection, Diagnostic.TABLES_WITH_MISSING_INDEXES);
    }

    /**
     * Returns tables with potentially missing indexes in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of tables with potentially missing indexes
     */
    @Override
    protected List<TableWithMissingIndex> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final long tableSize = rs.getLong(TABLE_SIZE);
            final long seqScans = rs.getLong("seq_scan");
            final long indexScans = rs.getLong("idx_scan");
            return TableWithMissingIndex.of(tableName, tableSize, seqScans, indexScans);
        });
    }
}
