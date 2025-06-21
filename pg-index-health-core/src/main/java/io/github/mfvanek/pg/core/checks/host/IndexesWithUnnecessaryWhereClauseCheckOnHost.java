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
 * Check for indexes with unnecessary where-clause on not null column on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public class IndexesWithUnnecessaryWhereClauseCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    public IndexesWithUnnecessaryWhereClauseCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.INDEXES_WITH_UNNECESSARY_WHERE_CLAUSE);
    }

    /**
     * Returns indexes with unnecessary where-clause on not null column in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of indexes with unnecessary where-clause on not null column
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithColumnsExtractor.of());
    }
}
