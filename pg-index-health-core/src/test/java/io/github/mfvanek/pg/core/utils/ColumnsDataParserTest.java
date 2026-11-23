/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.fixtures.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ColumnsDataParserTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ColumnsDataParser.class))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionWhenPassedInvalidData() {
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Columns array cannot be empty");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "abracadabra"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from abracadabra");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "a; b; c"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from a; b; c");
    }

    @Test
    void shouldThrowExceptionWhenTypeIsMissing() {
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1,true"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from c1,true");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", ",true"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from ,true");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnsInTable("t", "c1,true,"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("columnType cannot be blank");
    }

    @Test
    void shouldWorkWhenValidDataPassed() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1, true, bigint", "c2, false, text", "c3, abracadabra, integer[]"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1", PgColumnType.BIGINT),
                Column.ofNullable("t", "c2", PgColumnType.TEXT),
                Column.ofNullable("t", "c3", "integer[]"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithoutSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1,true,bigint", "c2,false,text", "c3,abracadabra,integer[]"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1", PgColumnType.BIGINT),
                Column.ofNullable("t", "c2", PgColumnType.TEXT),
                Column.ofNullable("t", "c3", "integer[]"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExtraSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "  c1,  true  , timestamp with time zone ", "   c2,   false   ,  character varying  "))
            .hasSize(2)
            .containsExactly(
                Column.ofNotNull("t", "c1", PgColumnType.TIMESTAMPTZ),
                Column.ofNullable("t", "c2", PgColumnType.VARCHAR))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithQuotedNamesContainingCommas() {
        assertThat(ColumnsDataParser.parseRawColumnsInTable("t", "\"Name\",false,text", "\"a,b\",true,integer", "\"x,true,y\",false,boolean"))
            .hasSize(3)
            .containsExactly(
                Column.ofNullable("t", "\"Name\"", PgColumnType.TEXT),
                Column.ofNotNull("t", "\"a,b\"", PgColumnType.INTEGER),
                Column.ofNullable("t", "\"x,true,y\"", PgColumnType.BOOLEAN))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExpressions() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "id,true,integer", "\"date_trunc('day'::text, ts)\",true,timestamp without time zone", "name,false,text"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "id", PgColumnType.INTEGER),
                Column.ofNotNull("t", "\"date_trunc('day'::text, ts)\"", PgColumnType.TIMESTAMP),
                Column.ofNullable("t", "name", PgColumnType.TEXT))
            .isUnmodifiable();
    }

    @Test
    void shouldAllowZeroColumnsInTable() {
        assertThat(ColumnsDataParser.parseRawColumnsInTable("t"))
            .isEmpty();
    }
}
