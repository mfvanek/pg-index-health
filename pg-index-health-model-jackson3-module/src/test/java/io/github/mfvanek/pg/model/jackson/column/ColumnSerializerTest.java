/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.column;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationOfNotNullColumnShouldWork() throws IOException {
        final Column original = Column.ofNotNull("table1", "column1");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"tableName\":\"table1\",\"columnName\":\"column1\",\"notNull\":true}");
        final Column restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), Column.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void serializationOfNullableColumnShouldWork() throws IOException {
        final Column original = Column.ofNullable("table2", "column2");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"tableName\":\"table2\",\"columnName\":\"column2\",\"notNull\":false}");
        final Column restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), Column.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: tableName");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\"}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: columnName");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"columnName\":\"column1\"}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: notNull");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"columnName\":\"column1\",\"notNull\":null}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: notNull");
    }

    @Test
    void deserializationShouldThrowExceptionOnWrongFieldType() {
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":11}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'tableName' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"columnName\":12}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'columnName' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"columnName\":\"column1\",\"notNull\":13}", Column.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'notNull' must be a boolean");
    }
}
