/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.table;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.jackson.support.ObjectMapperTestBase;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TableWithColumnsSerializerTest extends ObjectMapperTestBase {

    @Test
    void serializationShouldWork() throws IOException {
        final TableWithColumns original = TableWithColumns.of(Table.of("t1", 123L), List.of(
            Column.ofNotNull("t1", "c1"),
            Column.ofNotNull("t1", "c2")));
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("""
                {"table":{"tableName":"t1","tableSizeInBytes":123},"columns":[\
                {"tableName":"t1","columnName":"c1","notNull":true},\
                {"tableName":"t1","columnName":"c2","notNull":true}]}""");
        final TableWithColumns restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), TableWithColumns.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }

    @Test
    void serializationShouldWorkForTableWithoutColumns() throws IOException {
        final TableWithColumns original = TableWithColumns.withoutColumns(Table.of("t1", 123L));
        assertThat(objectMapper.writeValueAsString(original))
            .isEqualTo("{\"table\":{\"tableName\":\"t1\",\"tableSizeInBytes\":123},\"columns\":[]}");
        final TableWithColumns restored = objectMapper.readValue(objectMapper.writeValueAsBytes(original), TableWithColumns.class);
        assertThat(restored)
            .usingRecursiveComparison()
            .isEqualTo(original);
    }
}
