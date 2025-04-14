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
import javax.annotation.Nonnull;

/**
 * Check for columns whose names do not follow naming convention (that have to be enclosed in double-quotes) on a specific host.
 * <p>
 * You should avoid using quoted column names.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS">PostgreSQL Naming Convention</a>
 * @since 0.14.6
 */
public class ColumnsNotFollowingNamingConventionCheckOnHost extends AbstractCheckOnHost<Column> {

    public ColumnsNotFollowingNamingConventionCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_NOT_FOLLOWING_NAMING_CONVENTION);
    }

    /**
     * Returns columns whose names do not follow naming convention in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns whose names do not follow naming convention
     */
    @Nonnull
    @Override
    protected List<Column> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
