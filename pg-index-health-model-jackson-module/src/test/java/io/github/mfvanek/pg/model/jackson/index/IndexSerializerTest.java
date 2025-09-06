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

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: tableName");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\"}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: indexName");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"indexName\":\"index1\"}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: indexSizeInBytes");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"indexName\":\"index1\",\"indexSizeInBytes\":null}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: indexSizeInBytes");
    }

    @Test
    void deserializationShouldThrowExceptionOnWrongFieldType() {
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":11}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'tableName' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"indexName\":12}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'indexName' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"tableName\":\"table1\",\"indexName\":\"index1\",\"indexSizeInBytes\":\"13\"}", Index.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'indexSizeInBytes' must be a long");
    }
}
