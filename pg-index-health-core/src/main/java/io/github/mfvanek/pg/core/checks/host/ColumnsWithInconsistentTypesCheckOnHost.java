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
import io.github.mfvanek.pg.core.checks.extractors.ColumnWithTypeExtractor;
import io.github.mfvanek.pg.model.column.ColumnWithType;

/**
 * Check for columns that share the same name but have different data types across tables on a specific host.
 * Inconsistent types for the same column name make joins and application code error-prone.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.schemacrawler.com/lint.html">SchemaCrawler LinterColumnTypes</a>
 * @since 0.41.1
 */
public class ColumnsWithInconsistentTypesCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithInconsistentTypesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithInconsistentTypesCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, Diagnostic.COLUMNS_WITH_INCONSISTENT_TYPES, ColumnWithTypeExtractor.of());
    }
}
