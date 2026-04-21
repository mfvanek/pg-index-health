/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.function;

import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class StoredFunctionSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final StoredFunction original = StoredFunction.of("demo.custom_func", "(int a, int b)");
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"functionName\":\"demo.custom_func\",\"functionSignature\":\"(int a, int b)\"}");
        final StoredFunction restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), StoredFunction.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
