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
 * Check for unlogged tables (including unlogged partitioned tables) on a specific host.
 * <p>
 * Unlogged tables are not backed by WAL, so data in them is not replicated to standbys
 * and will be truncated automatically after a server crash.
 *
 * @author someshk1703
 * @see <a href="https://www.postgresql.org/docs/current/sql-createtable.html#SQL-CREATETABLE-UNLOGGED">CREATE TABLE UNLOGGED</a>
 * @since 0.41.2
 */
public class UnloggedTablesCheckOnHost extends AbstractCheckOnHost<Table> {

    /**
     * Constructs a new instance of {@code UnloggedTablesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public UnloggedTablesCheckOnHost(final PgConnection pgConnection) {
        super(Table.class, pgConnection, Diagnostic.UNLOGGED_TABLES, TableExtractor.of());
    }
}
