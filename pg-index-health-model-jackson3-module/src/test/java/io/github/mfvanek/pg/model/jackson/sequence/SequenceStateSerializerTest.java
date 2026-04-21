/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.sequence;

import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SequenceStateSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final SequenceState original = SequenceState.of("demo.seq1", "bigint", 83.21);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"sequenceName\":\"demo.seq1\",\"dataType\":\"bigint\",\"remainingPercentage\":83.21}");
        final SequenceState restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), SequenceState.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void deserializationShouldThrowExceptionOnMissingFields() {
        assertThatThrownBy(() -> objectMapper.readValue("{}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: sequenceName");
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":\"seq1\"}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: dataType");
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":\"seq1\",\"dataType\":\"bigint\"}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: remainingPercentage");
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":\"seq1\",\"dataType\":\"bigint\",\"remainingPercentage\":null}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Missing required field: remainingPercentage");
    }

    @Test
    void deserializationShouldThrowExceptionOnWrongFieldType() {
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":11}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'sequenceName' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":\"seq1\",\"dataType\":12}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'dataType' must be a string");
        assertThatThrownBy(() -> objectMapper.readValue("{\"sequenceName\":\"seq1\",\"dataType\":\"bigint\",\"remainingPercentage\":\"1\"}", SequenceState.class))
            .isInstanceOf(MismatchedInputException.class)
            .hasMessageStartingWith("Field 'remainingPercentage' must be a double");
    }

    @Test
    void acceptsNumbers() throws IOException {
        final String json = "{\"sequenceName\":\"seq1\",\"dataType\":\"bigint\",\"remainingPercentage\":88}";
        final SequenceState restored = objectMapper.readValue(json, SequenceState.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(SequenceState.of("seq1", "bigint", 88.0));
    }
}
