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
import io.github.mfvanek.pg.core.checks.extractors.ForeignKeyExtractor;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for foreign keys where the type of the constrained column does not match the type in the referenced table on a specific host.
 * <p>
 * The column types in the referring and target relation must match.
 * For example, a column with the {@code integer} type should refer to a column with the {@code integer} type.
 * This eliminates unnecessary conversions at the DBMS level and in the application code,
 * and reduces the number of errors that may appear due to type inconsistencies in the future.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/catalog-pg-constraint.html">pg_constraint</a>
 * @since 0.13.2
 */
public class ForeignKeysWithUnmatchedColumnTypeCheckOnHost extends AbstractCheckOnHost<ForeignKey> {

    /**
     * Constructs a new instance of {@code ForeignKeysWithUnmatchedColumnTypeCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ForeignKeysWithUnmatchedColumnTypeCheckOnHost(final PgConnection pgConnection) {
        super(ForeignKey.class, pgConnection, Diagnostic.FOREIGN_KEYS_WITH_UNMATCHED_COLUMN_TYPE);
    }

    /**
     * Returns foreign keys where the type of the constrained column does not match the type in the referenced table.
     * <p>
     * For multi-column constraints returns only columns with differences.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of foreign keys where the type of the constrained column does not match the type in the referenced table
     */
    @Override
    protected List<ForeignKey> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ForeignKeyExtractor.ofDefault());
    }
}
