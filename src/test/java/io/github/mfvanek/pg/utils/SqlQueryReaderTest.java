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

import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class SqlQueryReaderTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(SqlQueryReader.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getQueryFromFileShouldFindFileAndReadIt() {
        final String query = SqlQueryReader.getQueryFromFile("bloated_tables.sql");
        assertThat(query)
                .isNotNull()
                .hasSizeGreaterThan(1_000);
    }

    @Test
    void getQueryFromFileShouldFindFileInUppercase() {
        final String query = SqlQueryReader.getQueryFromFile("BLOATED_TABLES.SQL");
        assertThat(query)
                .isNotNull()
                .hasSizeGreaterThan(1_000);
    }

    @Test
    void getQueryFromFileShouldFailWithFileNotFound() {
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("unknown_file.sql"))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(FileNotFoundException.class)
                .hasMessage("java.io.FileNotFoundException: unknown_file.sql");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidFileName() {
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("sqlFileName cannot be null");
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sqlFileName cannot be blank");
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("sqlFileName cannot be blank");
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("file.txt"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("only *.sql files are supported");
    }
}
