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

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IndexWithColumnsSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final List<Column> columns = List.of(
            Column.ofNullable("t3", "t"),
            Column.ofNullable("t3", "f"));
        final IndexWithColumns original = IndexWithColumns.ofColumns("t3", "i3", 2L, columns);
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"index":{"tableName":"t3","indexName":"i3","indexSizeInBytes":2},"columns":[\
                {"tableName":"t3","columnName":"t","notNull":false},\
                {"tableName":"t3","columnName":"f","notNull":false}]}""");
        final IndexWithColumns restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), IndexWithColumns.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
