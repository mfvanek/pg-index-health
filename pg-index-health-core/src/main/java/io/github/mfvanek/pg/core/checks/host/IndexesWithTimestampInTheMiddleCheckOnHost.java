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
 * Check for indexes in which columns with the timestamp\timestamptz type are not the last on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.2
 */
public class IndexesWithTimestampInTheMiddleCheckOnHost extends AbstractCheckOnHost<IndexWithColumns> {

    /**
     * Constructs a new instance of {@code IndexesWithTimestampInTheMiddleCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public IndexesWithTimestampInTheMiddleCheckOnHost(final PgConnection pgConnection) {
        super(IndexWithColumns.class, pgConnection, Diagnostic.INDEXES_WITH_TIMESTAMP_IN_THE_MIDDLE);
    }

    /**
     * Returns indexes with timestamp\timestamptz in the middle in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of indexes with timestamp\timestamptz in the middle
     */
    @Override
    protected List<IndexWithColumns> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, IndexWithColumnsExtractor.of());
    }
}
