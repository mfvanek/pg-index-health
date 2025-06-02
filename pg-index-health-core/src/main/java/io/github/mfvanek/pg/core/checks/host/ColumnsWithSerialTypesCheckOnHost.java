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
import io.github.mfvanek.pg.core.checks.extractors.ColumnWithSerialTypeExtractor;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for columns of serial types that are not primary keys on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.2
 */
public class ColumnsWithSerialTypesCheckOnHost extends AbstractCheckOnHost<ColumnWithSerialType> {

    public ColumnsWithSerialTypesCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithSerialType.class, pgConnection, Diagnostic.COLUMNS_WITH_SERIAL_TYPES);
    }

    /**
     * Returns columns with serial types that are not primary keys in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with serial types that are not primary keys
     */
    @Override
    protected List<ColumnWithSerialType> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithSerialTypeExtractor.of());
    }
}
