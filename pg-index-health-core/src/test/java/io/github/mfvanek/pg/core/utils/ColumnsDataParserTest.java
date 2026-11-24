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
import io.github.mfvanek.pg.model.context.PgContext;
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
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1, true, bigint", "c2, false, text", "c3, abracadabra, integer[]"))
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.ofBigint(ctx, "t", "c1"),
                Column.ofNullableText(ctx, "t", "c2"),
                Column.ofNullable(ctx, "t", "c3", "integer[]"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithoutSpaces() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1,true,bigint", "c2,false,text", "c3,abracadabra,integer[]"))
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.ofBigint(ctx, "t", "c1"),
                Column.ofNullableText(ctx, "t", "c2"),
                Column.ofNullable(ctx, "t", "c3", "integer[]"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExtraSpaces() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "  c1,  true  , timestamp with time zone ", "   c2,   false   ,  character varying  "))
            .hasSize(2)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.ofTimestamptz(ctx, "t", "c1"),
                Column.ofNullableVarchar(ctx, "t", "c2"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithQuotedNamesContainingCommas() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ColumnsDataParser.parseRawColumnsInTable("t", "\"Name\",false,text", "\"a,b\",true,integer", "\"x,true,y\",false,boolean"))
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.ofNullableText(ctx, "t", "\"Name\""),
                Column.ofInteger(ctx, "t", "\"a,b\""),
                Column.ofNullableBoolean(ctx, "t", "\"x,true,y\""))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExpressions() {
        final PgContext ctx = PgContext.ofDefault();
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "id,true,integer", "\"date_trunc('day'::text, ts)\",true,timestamp without time zone", "name,false,text"))
            .hasSize(3)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                Column.ofInteger(ctx, "t", "id"),
                Column.ofTimestamp(ctx, "t", "\"date_trunc('day'::text, ts)\""),
                Column.ofNullableText(ctx, "t", "name"))
            .isUnmodifiable();
    }

    @Test
    void shouldAllowZeroColumnsInTable() {
        assertThat(ColumnsDataParser.parseRawColumnsInTable("t"))
            .isEmpty();
    }
}
