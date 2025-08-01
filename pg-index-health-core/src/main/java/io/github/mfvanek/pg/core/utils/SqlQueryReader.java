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

public final class SqlQueryReader {

    private static final Logger LOGGER = Logger.getLogger(SqlQueryReader.class.getName());

    private SqlQueryReader() {
        throw new UnsupportedOperationException();
    }

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

    private static String validateSqlFileName(final String sqlFileName) {
        final String fileName = Validators.notBlank(sqlFileName, "sqlFileName").toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".sql")) {
            throw new IllegalArgumentException("only *.sql files are supported");
        }
        return fileName;
    }
}
