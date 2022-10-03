/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.support.TestUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ColumnsInForeignKeyParserTest {

    @Test
    void privateConstructor() {
        assertThatThrownBy(() -> TestUtils.invokePrivateConstructor(ColumnsInForeignKeyParser.class))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldThrowExceptionWhenPassedInvalidData() {
        assertThatThrownBy(() -> ColumnsInForeignKeyParser.parseRawColumnData(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> ColumnsInForeignKeyParser.parseRawColumnData("t"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Columns array cannot be empty");
        assertThatThrownBy(() -> ColumnsInForeignKeyParser.parseRawColumnData("t", "abracadabra"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot parse column info from abracadabra");
        assertThatThrownBy(() -> ColumnsInForeignKeyParser.parseRawColumnData("t", "a, b, c"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot parse column info from a, b, c");
    }

    @Test
    void shouldWorkWhenValidDataPassed() {
        final List<Column> columns = ColumnsInForeignKeyParser.parseRawColumnData("t", "c1, true", "c2, false", "c3, abracadabra");
        assertThat(columns)
                .isNotNull()
                .hasSize(3)
                .containsExactly(
                        Column.ofNotNull("t", "c1"),
                        Column.ofNullable("t", "c2"),
                        Column.ofNullable("t", "c3"));
    }
}
