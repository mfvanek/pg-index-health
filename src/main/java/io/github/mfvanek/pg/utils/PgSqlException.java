/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import java.sql.SQLException;
import javax.annotation.Nonnull;

/**
 * Custom unchecked exception for SQL errors.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public class PgSqlException extends RuntimeException {

    private static final long serialVersionUID = -6917248037245766939L;

    public PgSqlException(@Nonnull final SQLException cause) {
        super(cause.getMessage(), cause);
    }
}
