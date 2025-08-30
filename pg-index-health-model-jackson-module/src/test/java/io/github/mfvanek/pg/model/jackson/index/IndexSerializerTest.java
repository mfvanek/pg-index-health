/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.index;

import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class IndexSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final Index original = Index.of("demo.custom_table", "demo.custom_index", 123L);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"tableName\":\"demo.custom_table\",\"indexName\":\"demo.custom_index\",\"indexSizeInBytes\":123}");
        final Index restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), Index.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
