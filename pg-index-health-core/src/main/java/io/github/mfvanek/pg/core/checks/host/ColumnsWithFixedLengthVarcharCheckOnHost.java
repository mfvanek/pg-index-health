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
 * Check for columns with fixed length varchar type on a specific host.
 *
 * @author Diana Gilfanova
 * @since 0.14.6
 */
public class ColumnsWithFixedLengthVarcharCheckOnHost extends AbstractCheckOnHost<Column> {

    public ColumnsWithFixedLengthVarcharCheckOnHost(final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITH_FIXED_LENGTH_VARCHAR);
    }

    /**
     * Returns columns with fixed length varchar type in the specified schema.
     * These are candidates for conversion to the varchar or text type.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with fixed length varchar type
     */
    @Override
    protected List<Column> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
