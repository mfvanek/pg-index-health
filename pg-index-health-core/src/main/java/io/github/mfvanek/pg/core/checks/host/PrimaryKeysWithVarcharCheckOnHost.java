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
import io.github.mfvanek.pg.core.checks.extractors.IndexWithColumnsExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.index.IndexWithColumns;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for primary keys with columns of fixed length varchar(32/36/38) type on a specific host.
 * <p>
 * Usually these columns should use built-in uuid type.
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

    public PrimaryKeysWithVarcharCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.PRIMARY_KEYS_WITH_VARCHAR);
    }

    /**
     * Returns primary keys with columns of fixed length varchar in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of primary keys with columns of fixed length varchar
     */
    @Nonnull
    @Override
    protected List<IndexWithColumns> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithColumnsExtractor.of());
    }
}
