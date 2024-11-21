/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.core.checks.extractors.ForeignKeyExtractor;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for foreign keys without associated indexes on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.6.0
 */
public class ForeignKeysNotCoveredWithIndexCheckOnHost extends AbstractCheckOnHost<ForeignKey> {

    public ForeignKeysNotCoveredWithIndexCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(ForeignKey.class, pgConnection, Diagnostic.FOREIGN_KEYS_WITHOUT_INDEX);
    }

    /**
     * Returns foreign keys without associated indexes in the specified schema.
     * <p>
     * For multi-column constraints returns all columns.
     *
     * @param pgContext check's context with the specified schema
     * @return list of foreign keys without associated indexes
     */
    @Nonnull
    @Override
    protected List<ForeignKey> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, ForeignKeyExtractor.ofDefault());
    }
}
