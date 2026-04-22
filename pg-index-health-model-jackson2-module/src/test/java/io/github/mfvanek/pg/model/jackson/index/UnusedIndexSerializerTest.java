/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.index;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnusedIndexSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final UnusedIndex original = UnusedIndex.of("t1", "i1", 100L, 4567L);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"index\":{\"tableName\":\"t1\",\"indexName\":\"i1\",\"indexSizeInBytes\":100},\"indexScans\":4567}");
        final UnusedIndex restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), UnusedIndex.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", UnusedIndex.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: index");
        assertThatThrownBy(() -> objectMapper.readValue("{\"index\":{\"tableName\":\"t1\",\"indexName\":\"i1\",\"indexSizeInBytes\":100}}", UnusedIndex.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: indexScans");
    }
}
