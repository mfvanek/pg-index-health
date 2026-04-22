/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.table;

import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TableWithBloatSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final TableWithBloat original = TableWithBloat.of("demo.table1", 143L, 256L, 56.78);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"table\":{\"tableName\":\"demo.table1\",\"tableSizeInBytes\":143},\"bloatSizeInBytes\":256,\"bloatPercentage\":56.78}");
        final TableWithBloat restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), TableWithBloat.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
