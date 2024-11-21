/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.validation.Validators;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nonnull;

public final class SqlQueryReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlQueryReader.class);

    private SqlQueryReader() {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    public static String getQueryFromFile(@Nonnull final String sqlFileName) {
        final String fileName = validateSqlFileName(sqlFileName);
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("sql/" + fileName)) {
            if (inputStream == null) {
                throw new FileNotFoundException(fileName);
            }
            final String sqlQueryFromFile = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            LOGGER.trace("Query from file {}", sqlQueryFromFile);
            return NamedParametersParser.parse(sqlQueryFromFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex); //NOSONAR
        }
    }

    @Nonnull
    private static String validateSqlFileName(@Nonnull final String sqlFileName) {
        final String fileName = Validators.notBlank(sqlFileName, "sqlFileName").toLowerCase(Locale.ROOT);
        if (!fileName.endsWith(".sql")) {
            throw new IllegalArgumentException("only *.sql files are supported");
        }
        return fileName;
    }
}
