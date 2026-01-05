/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.generator.support.GeneratorTestBase;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnNameAware;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DropDefaultValueGeneratorTest extends GeneratorTestBase {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldHandleInvalidArguments() {
        assertThatThrownBy(() -> new DropDefaultValueGenerator(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("options cannot be null");

        final DropDefaultValueGenerator generator = new DropDefaultValueGenerator(GeneratingOptions.builder().build());
        assertThatThrownBy(() -> generator.generate(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
    }

    @Test
    void generateForColumnLowerCase() {
        final DropDefaultValueGenerator generator = new DropDefaultValueGenerator(GeneratingOptions.builder().build());
        assertThat(generator.generate(column()))
            .isEqualTo(normalizeEndings("""
                alter table if exists s1.t1
                    alter column col1 drop default;"""));
    }

    @Test
    void generateWithoutBreakingLines() {
        final DropDefaultValueGenerator generator = new DropDefaultValueGenerator(
            GeneratingOptions.builder()
                .doNotBreakLines()
                .build());
        assertThat(generator.generate(column()))
            .isEqualTo("alter table if exists s1.t1 alter column col1 drop default;");
    }

    @Test
    void generateForColumnUpperCase() {
        final DropDefaultValueGenerator generator = new DropDefaultValueGenerator(
            GeneratingOptions.builder()
                .uppercaseForKeywords()
                .doNotBreakLines()
                .build());
        assertThat(generator.generate(column()))
            .isEqualTo("ALTER TABLE IF EXISTS s1.t1 ALTER COLUMN col1 DROP DEFAULT;");
    }

    @NonNull
    private ColumnNameAware column() {
        return Column.ofNotNull("s1.t1", "col1");
    }
}
