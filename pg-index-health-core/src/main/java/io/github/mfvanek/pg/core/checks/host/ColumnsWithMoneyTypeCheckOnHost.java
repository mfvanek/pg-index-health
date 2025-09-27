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
import io.github.mfvanek.pg.core.checks.extractors.ColumnExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for columns with {@code money} type on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.1
 */
public class ColumnsWithMoneyTypeCheckOnHost extends AbstractCheckOnHost<Column> {

    /**
     * Constructs a new instance of {@code ColumnsWithMoneyTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithMoneyTypeCheckOnHost(final PgConnection pgConnection) {
        super(Column.class, pgConnection, Diagnostic.COLUMNS_WITH_MONEY_TYPE);
    }

    /**
     * Returns columns with a money type in the specified schema.
     * These are candidates for conversion to the {@code numeric} type.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with a money type
     * @see <a href="https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_money">Do not use money</a>
     */
    @Override
    protected List<Column> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
