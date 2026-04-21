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
import io.github.mfvanek.pg.model.table.Table;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TableSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final Table original = Table.of("demo.table1", 143L);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"tableName\":\"demo.table1\",\"tableSizeInBytes\":143}");
        final Table restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), Table.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
