/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.checks.extractors.ColumnExtractor;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.column.Column;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for columns with {@code json} type on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.1
 */
public class ColumnsWithJsonTypeCheckOnHost extends AbstractCheckOnHost<Column> {

    public ColumnsWithJsonTypeCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITH_JSON_TYPE);
    }

    /**
     * Returns columns with json type in the specified schema.
     * These are candidates for conversion to the {@code jsonb} type.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with json type
     * @see <a href="https://www.postgresql.org/docs/current/datatype-json.html">JSON Types</a>
     */
    @Nonnull
    @Override
    protected List<Column> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
