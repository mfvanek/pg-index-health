/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.exception;

import java.io.Serial;
import java.sql.SQLException;

/**
 * Custom unchecked exception for SQL errors.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
public class PgSqlException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8740709401886468019L;

    /**
     * Constructs a new {@code PgSqlException} with the specified cause.
     *
     * @param cause the {@link SQLException} that caused this exception; must not be null.
     */
    public PgSqlException(final SQLException cause) {
        super(cause.getMessage(), cause);
    }
}
