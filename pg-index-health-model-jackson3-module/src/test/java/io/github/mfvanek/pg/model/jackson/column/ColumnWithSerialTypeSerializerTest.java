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
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnWithSerialTypeSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final ColumnWithSerialType original = ColumnWithSerialType.ofSmallSerial(Column.ofNotNull("t1", "c1"), "seq1");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"column":{"tableName":"t1","columnName":"c1","notNull":true},\
                "columnType":"smallserial","serialType":"SMALL_SERIAL","sequenceName":"seq1"}""");
        final ColumnWithSerialType restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), ColumnWithSerialType.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", ColumnWithSerialType.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: column");
        assertThatThrownBy(() -> objectMapper.readValue("""
            {"column":{"tableName":"t1","columnName":"c1","notNull":true}}""", ColumnWithSerialType.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: serialType");
        assertThatThrownBy(() -> objectMapper.readValue("""
            {"column":{"tableName":"t1","columnName":"c1","notNull":true},\
            "columnType":"smallserial","serialType":"SMALL_SERIAL"}""", ColumnWithSerialType.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: sequenceName");
    }
}
