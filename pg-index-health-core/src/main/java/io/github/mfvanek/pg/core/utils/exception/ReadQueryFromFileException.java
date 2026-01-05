/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils.exception;

import java.io.IOException;
import java.io.Serial;

/**
 * Custom unchecked exception for IO errors while reading sql query from file.
 *
 * @author Ivan Vakhrushev
 * @since 0.15.0
 */
public class ReadQueryFromFileException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2030407491772154787L;

    /**
     * Constructs a new {@code ReadQueryFromFileException} with the specified SQL file name and the cause of the error.
     *
     * @param sqlFileName the name of the SQL file that caused the exception; must not be null
     * @param cause       the underlying {@code IOException} that caused this exception; must not be null
     */
    public ReadQueryFromFileException(final String sqlFileName, final IOException cause) {
        super("Error occurred while reading sql query from file " + sqlFileName, cause);
    }
}
