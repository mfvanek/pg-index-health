/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for columns with {@code timestamp} or {@code timetz} type on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class ColumnsWithTimestampOrTimetzTypeCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithTimestampOrTimetzTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithTimestampOrTimetzTypeCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, Diagnostic.COLUMNS_WITH_TIMESTAMP_OR_TIMETZ_TYPE);
    }

    /**
     * Returns columns with {@code timestamp} or {@code timetz} type in the specified schema.
     * These are candidates for conversion to the {@code timestamptz} type.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with timestamp or timetz type
     * @see <a href="https://wiki.postgresql.org/wiki/Don't_Do_This#Don.27t_use_timestamp_.28without_time_zone.29">Don't use timestamp (without time zone)</a>
     * @see <a href="https://wiki.postgresql.org/wiki/Don't_Do_This#Don't_use_timetz">Don't use timetz</a>
     */
    @Override
    protected List<ColumnWithType> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithTypeExtractor.of());
    }
}
