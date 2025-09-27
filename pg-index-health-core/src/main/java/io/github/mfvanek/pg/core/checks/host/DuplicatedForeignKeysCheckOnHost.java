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
import io.github.mfvanek.pg.core.checks.extractors.DuplicatedForeignKeysExtractor;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.context.PgContext;

import java.util.List;

/**
 * Check for duplicated (completely identical) foreign keys on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.1
 */
public class DuplicatedForeignKeysCheckOnHost extends AbstractCheckOnHost<DuplicatedForeignKeys> {

    /**
     * Constructs a new instance of {@code DuplicatedForeignKeysCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public DuplicatedForeignKeysCheckOnHost(final PgConnection pgConnection) {
        super(DuplicatedForeignKeys.class, pgConnection, Diagnostic.DUPLICATED_FOREIGN_KEYS);
    }

    /**
     * Returns duplicated (completely identical) foreign keys in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of duplicated foreign keys
     */
    @Override
    protected List<DuplicatedForeignKeys> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, DuplicatedForeignKeysExtractor.of("duplicate"));
    }
}
