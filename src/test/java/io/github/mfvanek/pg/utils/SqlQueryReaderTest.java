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

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SqlQueryReaderTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(SqlQueryReader.class)).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void getQueryFromFileShouldFindFileAndReadIt() {
        String query = SqlQueryReader.getQueryFromFile("bloated_tables.sql");
        assertThat(query).isNotNull();
        assertThat(query.length()).isGreaterThan(1_000);
    }

    @Test
    void getQueryFromFileShouldFindFileInUppercase() {
        String query = SqlQueryReader.getQueryFromFile("BLOATED_TABLES.SQL");
        assertThat(query).isNotNull();
        assertThat(query.length()).isGreaterThan(1_000);
    }

    @Test
    void getQueryFromFileShouldFailWithFileNotFound() {
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("unknown_file.sql"))
                .isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(FileNotFoundException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidFileName() {
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile(null)).isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("   ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SqlQueryReader.getQueryFromFile("file.txt")).isInstanceOf(IllegalArgumentException.class);
    }
}
