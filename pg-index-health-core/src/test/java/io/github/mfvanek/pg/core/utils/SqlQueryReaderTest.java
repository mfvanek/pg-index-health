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

import io.github.mfvanek.pg.connection.fixtures.support.LogsCaptor;
import io.github.mfvanek.pg.core.utils.exception.ReadQueryFromFileException;
import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.logging.Level;

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
        try (LogsCaptor ignored = new LogsCaptor(SqlQueryReader.class, Level.FINEST)) {
            final String query = SqlQueryReader.getQueryFromFile("bloated_tables.sql");
            assertThat(query)
                .isNotNull()
                .hasSizeGreaterThan(1_000);
        }
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
            .isInstanceOf(ReadQueryFromFileException.class)
            .hasCauseInstanceOf(FileNotFoundException.class)
            .hasMessage("Error occurred while reading sql query from file unknown_file.sql");
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
