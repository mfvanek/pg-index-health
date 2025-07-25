/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.column.Column;
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
    void shouldWorkWhenValidDataPassed() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1, true", "c2, false", "c3, abracadabra"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"),
                Column.ofNullable("t", "c3"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithoutSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "c1,true", "c2,false", "c3,abracadabra"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"),
                Column.ofNullable("t", "c3"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExtraSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "  c1,  true  ", "   c2,   false   "))
            .hasSize(2)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExpressions() {
        assertThat(ColumnsDataParser.parseRawColumnsInForeignKeyOrIndex("t", "id,true", "\"date_trunc('day'::text, ts)\",true", "name,false"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "id"),
                Column.ofNotNull("t", "\"date_trunc('day'::text, ts)\""),
                Column.ofNullable("t", "name"))
            .isUnmodifiable();
    }
}
