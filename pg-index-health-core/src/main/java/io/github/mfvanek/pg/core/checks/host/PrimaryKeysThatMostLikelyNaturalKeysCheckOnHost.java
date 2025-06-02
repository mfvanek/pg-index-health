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

/**
 * Check for primary keys that are most likely natural keys on a specific host.
 * <p>
 * It is better to use surrogate keys instead of natural ones.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://blog.ploeh.dk/2024/06/03/youll-regret-using-natural-keys/">You'll regret using natural keys</a>
 * @since 0.15.0
 */
public class PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    public PrimaryKeysThatMostLikelyNaturalKeysCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.PRIMARY_KEYS_THAT_MOST_LIKELY_NATURAL_KEYS);
    }

    /**
     * Returns primary keys that are most likely natural keys in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of primary keys that are most likely natural keys
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithColumnsExtractor.of());
    }
}
