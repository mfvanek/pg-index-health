/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.core.utils.exception.ReadQueryFromFileException;
import io.github.mfvanek.pg.model.validation.Validators;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Utility class for reading SQL query files and parsing their content.
 * This class reads a SQL query from a file located in the "sql" resource directory,
 * validates the file name, and processes any named parameters in the query.
 * It is designed to be used with `.sql` files and throws exceptions for invalid operations.
 */
public final class SqlQueryReader {

    private static final Logger LOGGER = Logger.getLogger(SqlQueryReader.class.getName());

    private SqlQueryReader() {
        throw new UnsupportedOperationException();
    }

    /**
     * Reads a SQL query from a file located in the "sql" resource directory, validates the file name,
     * and processes the content to replace named parameters with placeholders.
     *
     * @param sqlFileName the name of the SQL file to read, must not be null, empty, or blank and should have a ".sql" extension
     * @return the SQL query as a string with named parameters replaced by placeholders
     */
    public static String getQueryFromFile(final String sqlFileName) {
        final String fileName = validateSqlFileName(sqlFileName);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("sql/" + fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException(fileName);
            }
            final String sqlQueryFromFile = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            LOGGER.finest(() -> "Query from file " + sqlQueryFromFile);
            return NamedParametersParser.parse(sqlQueryFromFile);
        } catch (IOException ex) {
            throw new ReadQueryFromFileException(sqlFileName, ex);
        }
    }

    /**
     * Constructs a SQL query by determining the corresponding SQL file name from the given check name
     * and reading the query from that file.
     *
     * @param checkName the name of the check to generate the SQL file name, must not be null, empty, or blank
     * @return the SQL query as a string after reading from the file and processing
     */
    public static String getQueryForCheck(final String checkName) {
        final String sqlFileName = buildSqlFileName(checkName);
        return getQueryFromFile(sqlFileName);
    }

    private static String buildSqlFileName(final String checkName) {
        return Validators.notBlank(checkName, "checkName").toLowerCase(Locale.ROOT) + ".sql";
    }

    private static String validateSqlFileName(final String sqlFileName) {
        final String fileName = Validators.notBlank(sqlFileName, "sqlFileName").toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".sql")) {
            throw new IllegalArgumentException("only *.sql files are supported");
        }
        return fileName;
    }
}
