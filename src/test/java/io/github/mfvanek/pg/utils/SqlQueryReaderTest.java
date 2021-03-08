/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SqlQueryReaderTest {

    @Test
    void privateConstructor() {
        assertThrows(UnsupportedOperationException.class, () -> TestUtils.invokePrivateConstructor(SqlQueryReader.class));
    }

    @Test
    void getQueryFromFileShouldFindFileAndReadIt() {
        String query = SqlQueryReader.getQueryFromFile("bloated_tables.sql");
        assertNotNull(query);
        assertThat(query.length(), greaterThan(1_000));
    }

    @Test
    void getQueryFromFileShouldFindFileInUppercase() {
        String query = SqlQueryReader.getQueryFromFile("BLOATED_TABLES.SQL");
        assertNotNull(query);
        assertThat(query.length(), greaterThan(1_000));
    }

    @Test
    void getQueryFromFileShouldFailWithFileNotFound() {
        final RuntimeException exception = assertThrows(RuntimeException.class,
                () -> SqlQueryReader.getQueryFromFile("unknown_file.sql"));
        assertNotNull(exception);
        assertThat(exception.getCause(), instanceOf(FileNotFoundException.class));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidFileName() {
        assertThrows(NullPointerException.class, () -> SqlQueryReader.getQueryFromFile(null));
        assertThrows(IllegalArgumentException.class, () -> SqlQueryReader.getQueryFromFile(""));
        assertThrows(IllegalArgumentException.class, () -> SqlQueryReader.getQueryFromFile("   "));
        assertThrows(IllegalArgumentException.class, () -> SqlQueryReader.getQueryFromFile("file.txt"));
    }
}
