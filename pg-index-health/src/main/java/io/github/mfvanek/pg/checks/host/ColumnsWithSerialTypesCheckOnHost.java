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

import io.github.mfvanek.pg.checks.extractors.ColumnWithSerialTypeExtractor;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for columns of serial types that are not primary keys on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.2
 */
public class ColumnsWithSerialTypesCheckOnHost extends AbstractCheckOnHost<ColumnWithSerialType> {

    public ColumnsWithSerialTypesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(ColumnWithSerialType.class, pgConnection, Diagnostic.COLUMNS_WITH_SERIAL_TYPES);
    }

    /**
     * Returns columns with serial types that are not primary keys in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with serial types that are not primary keys
     */
    @Nonnull
    @Override
    public List<ColumnWithSerialType> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithSerialTypeExtractor.of());
    }
}
