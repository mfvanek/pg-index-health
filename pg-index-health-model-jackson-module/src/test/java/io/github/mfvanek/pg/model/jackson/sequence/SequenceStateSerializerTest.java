/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.sequence;

import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class SequenceStateSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final SequenceState original = SequenceState.of("demo.seq1", "bigint", 83.21);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"sequenceName\":\"demo.seq1\",\"dataType\":\"bigint\",\"remainingPercentage\":83.21}");
        final SequenceState restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), SequenceState.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
