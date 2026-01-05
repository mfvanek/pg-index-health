/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
 * Check for columns of type {@code char}, {@code char(n)}, {@code character(n)} or {@code bpchar(n)} on a specific host.
 * <p>
 * Do not use the type {@code char(n)}. It is better to use the type {@code text} instead.
 * Any string you insert into a {@code char(n)} field will be padded with spaces to the declared width.
 * The space-padding does waste space but does not make operations on it any faster.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/datatype-character.html">Character Types </a>
 * @since 0.30.1
 */
public class ColumnsWithCharTypeCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithCharTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithCharTypeCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, Diagnostic.COLUMNS_WITH_CHAR_TYPE);
    }

    /**
     * Returns columns with a {@code character} type in the specified schema.
     * These are candidates for conversion to the {@code text} type.
     *
     * @param pgContext check's context with the specified schema
     * @return list of columns with a {@code character} type
     * @see <a href="https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don't_use_char(n)">Do not use char(n)</a>
     */
    @Override
    protected List<ColumnWithType> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithTypeExtractor.of());
    }
}
