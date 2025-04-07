/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnInForeignKey(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnInForeignKey("t"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Columns array cannot be empty");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnInForeignKey("t", "abracadabra"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from abracadabra");
        assertThatThrownBy(() -> ColumnsDataParser.parseRawColumnInForeignKey("t", "a, b, c"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Cannot parse column info from a, b, c");
    }

    @Test
    void shouldWorkWhenValidDataPassed() {
        assertThat(ColumnsDataParser.parseRawColumnInForeignKey("t", "c1, true", "c2, false", "c3, abracadabra"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"),
                Column.ofNullable("t", "c3"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithoutSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnInForeignKey("t", "c1,true", "c2,false", "c3,abracadabra"))
            .hasSize(3)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"),
                Column.ofNullable("t", "c3"))
            .isUnmodifiable();
    }

    @Test
    void shouldWorkWithExtraSpaces() {
        assertThat(ColumnsDataParser.parseRawColumnInForeignKey("t", "  c1,  true  ", "   c2,   false   "))
            .hasSize(2)
            .containsExactly(
                Column.ofNotNull("t", "c1"),
                Column.ofNullable("t", "c2"))
            .isUnmodifiable();
    }
}
