/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.column;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

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
}
