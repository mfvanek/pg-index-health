/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.context;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class PgContextSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final PgContext original = PgContext.of("demo", 0.15, 0.25);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"schemaName\":\"demo\",\"bloatPercentageThreshold\":0.15,\"remainingPercentageThreshold\":0.25}");
        final PgContext restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), PgContext.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
