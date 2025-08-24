/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.table;

import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TableWithMissingIndexSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final TableWithMissingIndex original = TableWithMissingIndex.of("t", 113L, 255L, 344L);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"table\":{\"tableName\":\"t\",\"tableSizeInBytes\":113},\"seqScans\":255,\"indexScans\":344}");
        final TableWithMissingIndex restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), TableWithMissingIndex.class);
        assertThat(restored)
            .isEqualTo(original)
            .satisfies(t -> {
                assertThat(t.getSeqScans())
                    .isEqualTo(255L);
                assertThat(t.getIndexScans())
                    .isEqualTo(344L);
            });
    }
}
