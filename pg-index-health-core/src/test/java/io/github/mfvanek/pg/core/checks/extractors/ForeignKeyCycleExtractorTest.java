/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.constraint.ForeignKeyCycle;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Tag("fast")
class ForeignKeyCycleExtractorTest {

    private final ResultSetExtractor<@NonNull ForeignKeyCycle> extractor = ForeignKeyCycleExtractor.of();

    @Test
    void shouldCreateInstance() {
        assertThat(extractor)
            .isNotNull()
            .isInstanceOf(ForeignKeyCycleExtractor.class);
    }

    @Test
    void shouldExtractCycleOfSingleColumnForeignKeys() throws SQLException {
        try (ResultSet rs = prepare(
            List.of("t1", "t2"),
            List.of("fk", "fk"),
            List.of("col1,true", "col2,false"))) {
            assertThat(extractor.extractData(rs))
                .usingRecursiveComparison()
                .isEqualTo(ForeignKeyCycle.of(
                    ForeignKey.ofNotNullColumn("t1", "fk", "col1"),
                    ForeignKey.ofNullableColumn("t2", "fk", "col2")));
        }
    }

    @Test
    void shouldGroupColumnsOfMulticolumnForeignKeys() throws SQLException {
        try (ResultSet rs = prepare(
            List.of("t1", "t1", "t2", "t3", "t3", "t3"),
            List.of("c1", "c1", "c2", "c3", "c3", "c3"),
            List.of("a,true", "b,false", "c,true", "d,true", "e,true", "f,false"))) {
            assertThat(extractor.extractData(rs))
                .usingRecursiveComparison()
                .isEqualTo(ForeignKeyCycle.of(
                    ForeignKey.of("t1", "c1", List.of(Column.ofNotNull("t1", "a"), Column.ofNullable("t1", "b"))),
                    ForeignKey.ofNotNullColumn("t2", "c2", "c"),
                    ForeignKey.of("t3", "c3", List.of(Column.ofNotNull("t3", "d"), Column.ofNotNull("t3", "e"), Column.ofNullable("t3", "f")))));
        }
    }

    @Test
    void shouldNotMergeDifferentForeignKeysOfTheSameTable() throws SQLException {
        try (ResultSet rs = prepare(
            List.of("t1", "t1", "t2"),
            List.of("c1", "c2", "c3"),
            List.of("a,true", "b,true", "c,true"))) {
            assertThatThrownBy(() -> extractor.extractData(rs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Table name must not be repeated within a cycle");
        }
    }

    @Test
    void shouldThrowExceptionWhenArraysHaveDifferentLength() throws SQLException {
        try (ResultSet rs = prepare(
            List.of("t1", "t2"),
            List.of("c1"),
            List.of("a,true", "b,true"))) {
            assertThatThrownBy(() -> extractor.extractData(rs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arrays table_names, constraint_names and columns must have the same length");
        }
        try (ResultSet rs = prepare(
            List.of("t1", "t2"),
            List.of("c1", "c2"),
            List.of("a,true"))) {
            assertThatThrownBy(() -> extractor.extractData(rs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Arrays table_names, constraint_names and columns must have the same length");
        }
    }

    @Test
    void shouldThrowExceptionOnEmptyArrays() throws SQLException {
        try (ResultSet rs = prepare(List.of(), List.of(), List.of())) {
            assertThatThrownBy(() -> extractor.extractData(rs))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("foreignKeys should contain at least two items");
        }
    }

    private static ResultSet prepare(final List<String> tableNames, final List<String> constraintNames, final List<String> columns) throws SQLException {
        final ResultSet rs = mock(ResultSet.class);
        mockArray(rs, ForeignKeyCycleExtractor.TABLE_NAMES, tableNames);
        mockArray(rs, ForeignKeyCycleExtractor.CONSTRAINT_NAMES, constraintNames);
        mockArray(rs, "columns", columns);
        return rs;
    }

    private static void mockArray(final ResultSet rs, final String columnName, final List<String> values) throws SQLException {
        final Array array = mock(Array.class);
        when(array.getArray()).thenReturn(values.toArray(String[]::new));
        when(rs.getArray(columnName)).thenReturn(array);
    }
}
