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
import io.github.mfvanek.pg.core.checks.extractors.ColumnExtractor;
import io.github.mfvanek.pg.model.column.Column;

/**
 * Check for columns with an enum type (including arrays of enums) on a specific host.
 * These are candidates for conversion to the {@code text} type with a check constraint or a foreign key to a lookup table.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/datatype-enum.html">Enumerated Types</a>
 * @since 0.50.0
 */
public class ColumnsWithEnumTypeCheckOnHost extends AbstractCheckOnHost<Column> {

    /**
     * Constructs a new instance of {@code ColumnsWithEnumTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithEnumTypeCheckOnHost(final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITH_ENUM_TYPE, ColumnExtractor.of());
    }
}
