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
import io.github.mfvanek.pg.core.checks.extractors.ColumnExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for columns without description on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public class ColumnsWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<Column> {

    public ColumnsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITHOUT_DESCRIPTION);
    }

    /**
     * Returns columns without description (comment) in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns without description
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">SQL Commands - COMMENT</a>
     */
    @Override
    protected List<Column> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
