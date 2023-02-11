/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.Column;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for columns without description on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class ColumnsWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<Column> {

    public ColumnsWithoutDescriptionCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITHOUT_DESCRIPTION);
    }

    /**
     * Returns columns without description (comment) in the specified schema.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-comment.html">SQL Commands - COMMENT</a>
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns without description
     */
    @Nonnull
    @Override
    public List<Column> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
