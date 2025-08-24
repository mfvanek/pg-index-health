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

import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicatedIndexesSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final DuplicatedIndexes original = DuplicatedIndexes.of(List.of(
            Index.of("t", "i1", 101L),
            Index.of("t", "i2", 202L)));
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"tableName":"t","totalSize":303,"indexes":[\
                {"tableName":"t","indexName":"i1","indexSizeInBytes":101},\
                {"tableName":"t","indexName":"i2","indexSizeInBytes":202}]}""");
        final DuplicatedIndexes restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), DuplicatedIndexes.class);
        assertThat(restored)
            .isEqualTo(original);
    }
}
