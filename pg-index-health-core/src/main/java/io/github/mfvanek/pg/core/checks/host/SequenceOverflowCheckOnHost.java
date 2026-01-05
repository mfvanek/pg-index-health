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
import io.github.mfvanek.pg.core.checks.extractors.SequenceStateExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import java.util.List;

/**
 * Check for sequence overflow on a specific host.
 *
 * @author Blohny
 * @since 0.12.0
 */
public class SequenceOverflowCheckOnHost extends AbstractCheckOnHost<SequenceState> {

    /**
     * Constructs a new instance of {@code SequenceOverflowCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public SequenceOverflowCheckOnHost(final PgConnection pgConnection) {
        super(SequenceState.class, pgConnection, Diagnostic.SEQUENCE_OVERFLOW);
    }

    /**
     * Returns sequences that are close to overflow in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of sequences close to overflow
     * @see SequenceState
     */
    @Override
    protected List<SequenceState> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, SequenceStateExtractor.of());
    }
}
