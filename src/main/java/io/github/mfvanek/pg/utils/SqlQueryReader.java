/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for analyzing and maintaining indexes health in PostgreSQL databases.
 */

package io.github.mfvanek.pg.utils;

import org.apache.commons.io.FileUtils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public final class SqlQueryReader {

    private SqlQueryReader() {
        throw new UnsupportedOperationException();
    }

    public static String getQueryFromFile(@Nonnull final String sqlFileName) {
        final String fileName = Validators.validateSqlFileName(sqlFileName);
        try {
            final ClassLoader classLoader = SqlQueryReader.class.getClassLoader();
            final URL resource = classLoader.getResource("sql/" + fileName);
            if (resource == null) {
                throw new FileNotFoundException(fileName);
            }
            final String pathToFile = resource.getFile();
            return FileUtils.readFileToString(new File(pathToFile), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
