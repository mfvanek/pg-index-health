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
 * Check for columns with {@code oid} or {@code lo} blob type on a specific host.
 * These are candidates for conversion to the {@code bytea} type.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/largeobjects.html">PostgreSQL Large Objects</a>
 * @see <a href="https://www.postgresql.org/docs/current/lo.html">lo extension</a>
 * @since 0.41.1
 */
public class ColumnsWithBlobTypeCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithBlobTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithBlobTypeCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, Diagnostic.COLUMNS_WITH_BLOB_TYPE, ColumnWithTypeExtractor.of());
    }
}
