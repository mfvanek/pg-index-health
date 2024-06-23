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
 * Check for primary keys columns with serial types (smallserial/serial/bigserial)
 * New "generated as identity" syntax should be used instead.
 *
 * @author Vadim Khizhin
 * @since 0.13.0
 */
public class PrimaryKeysWithSerialTypesCheckOnHost extends AbstractCheckOnHost<ColumnWithSerialType> {

    public PrimaryKeysWithSerialTypesCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(ColumnWithSerialType.class, pgConnection, Diagnostic.PRIMARY_KEYS_WITH_SERIAL_TYPES);
    }

    @Nonnull
    @Override
    public List<ColumnWithSerialType> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithSerialTypeExtractor.of());
    }
}
