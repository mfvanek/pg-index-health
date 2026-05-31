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
import io.github.mfvanek.pg.core.checks.extractors.TableWithColumnsExtractor;
import io.github.mfvanek.pg.model.table.TableWithColumns;

/**
 * Check for tables with incrementing column names (e.g., {@code phone1}, {@code phone2}) on a specific host.
 * Such columns indicate de-normalization that could be replaced with a separate child table and a foreign key.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.schemacrawler.com/lint.html">SchemaCrawler LinterTableWithIncrementingColumns</a>
 * @since 0.41.1
 */
public class TablesWithIncrementingColumnsCheckOnHost extends AbstractCheckOnHost<TableWithColumns> {

    /**
     * Constructs a new instance of {@code TablesWithIncrementingColumnsCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public TablesWithIncrementingColumnsCheckOnHost(final PgConnection pgConnection) {
        super(TableWithColumns.class, pgConnection, Diagnostic.TABLES_WITH_INCREMENTING_COLUMNS, TableWithColumnsExtractor.of());
    }
}
