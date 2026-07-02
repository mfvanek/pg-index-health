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
import io.github.mfvanek.pg.core.checks.extractors.AnyObjectExtractor;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

/**
 * Check for unlogged sequences on a specific host.
 * <p>
 * Unlogged sequences are not backed by WAL, so their state is not replicated to standbys
 * and will be reset automatically after a server crash.
 * Their current value is lost after a crash, which may cause duplicate key errors
 * if used as default values for columns.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/current/sql-createsequence.html">CREATE SEQUENCE</a>
 * @since 0.15.0
 */
public class UnloggedSequencesCheckOnHost extends AbstractCheckOnHost<AnyObject> {

    /**
     * Constructs a new instance of {@code UnloggedSequencesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public UnloggedSequencesCheckOnHost(final PgConnection pgConnection) {
        super(AnyObject.class, pgConnection, Diagnostic.UNLOGGED_SEQUENCES, AnyObjectExtractor.of());
    }
}
