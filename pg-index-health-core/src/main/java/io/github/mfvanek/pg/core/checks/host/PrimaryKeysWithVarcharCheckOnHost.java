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
import io.github.mfvanek.pg.core.checks.extractors.IndexWithColumnsExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;

/**
 * Check for primary keys with columns of a fixed length varchar(32/36/38) type on a specific host.
 * <p>
 * Usually these columns should use a built-in uuid type.
 * <p>
 * UUID representation:
 * <pre>{@code
 * b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b // with dashes - 36 characters
 * b9b1f6f57f904b68a389f0ad8bb5784b // without dashes - 32 characters
 * {b9b1f6f5-7f90-4b68-a389-f0ad8bb5784b} // with curly braces - 38 characters
 * }</pre>
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/17/datatype-uuid.html">UUID Type</a>
 * @since 0.14.6
 */
public class PrimaryKeysWithVarcharCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code PrimaryKeysWithVarcharCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public PrimaryKeysWithVarcharCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.PRIMARY_KEYS_WITH_VARCHAR);
    }

    /**
     * Returns primary keys with columns of fixed length varchar in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of primary keys with columns of fixed length varchar
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithColumnsExtractor.of());
    }
}
