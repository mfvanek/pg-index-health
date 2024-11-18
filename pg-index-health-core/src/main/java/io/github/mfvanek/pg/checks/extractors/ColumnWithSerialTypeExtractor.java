/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.SerialType;
import io.github.mfvanek.pg.model.context.PgContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import javax.annotation.Nonnull;

public class ColumnWithSerialTypeExtractor implements ResultSetExtractor<ColumnWithSerialType> {

    private static final String PUBLIC_SCHEMA_PREFIX = PgContext.DEFAULT_SCHEMA_NAME + '.';

    private final ResultSetExtractor<Column> columnExtractor;

    private ColumnWithSerialTypeExtractor() {
        this.columnExtractor = ColumnExtractor.of();
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ColumnWithSerialType extractData(@Nonnull final ResultSet resultSet) throws SQLException {
        final Column column = columnExtractor.extractData(resultSet);
        final String columnType = resultSet.getString("column_type");
        final String sequenceName = resultSet.getString("sequence_name");
        return ColumnWithSerialType.of(column, SerialType.valueFrom(columnType), normalizeSequenceName(sequenceName));
    }

    // Issue here is that pg_catalog.pg_sequence.seqrelid::regclass returns sequence name without 'public' schema prefix
    // but function 'pg_get_serial_sequence' does.
    //
    // See https://www.postgresql.org/docs/current/catalog-pg-sequence.html
    // See https://www.postgresql.org/docs/current/functions-info.html
    @Nonnull
    private static String normalizeSequenceName(@Nonnull final String sequenceName) {
        if (sequenceName.toLowerCase(Locale.ROOT).startsWith(PUBLIC_SCHEMA_PREFIX)) {
            return sequenceName.substring(PUBLIC_SCHEMA_PREFIX.length());
        }
        return sequenceName;
    }

    /**
     * Creates {@code ColumnWithSerialTypeExtractor} instance.
     *
     * @return {@code ColumnWithSerialTypeExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<ColumnWithSerialType> of() {
        return new ColumnWithSerialTypeExtractor();
    }
}
