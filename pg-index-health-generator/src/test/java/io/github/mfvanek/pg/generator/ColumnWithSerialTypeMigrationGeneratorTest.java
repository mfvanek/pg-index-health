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
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnWithSerialTypeMigrationGeneratorTest extends GeneratorTestBase {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldHandleInvalidArguments() {
        assertThatThrownBy(() -> new ColumnWithSerialTypeMigrationGenerator(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("options cannot be null");
    }

    @Test
    void forSingleColumn() {
        final ColumnWithSerialTypeMigrationGenerator generator = new ColumnWithSerialTypeMigrationGenerator(GeneratingOptions.builder().build());

        assertThat(generator.generate(List.of(column())))
            .hasSize(1)
            .containsExactly(normalizeEndings("""
                alter table if exists s1.t1
                    alter column col1 drop default;
                drop sequence if exists s1.seq1;"""));
    }

    @Test
    void forSeveralColumns() {
        final ColumnWithSerialType secondColumn = ColumnWithSerialType.ofSerial(Column.ofNotNull("s2.t2", "col2"), "s2.seq2");
        final ColumnWithSerialTypeMigrationGenerator generator = new ColumnWithSerialTypeMigrationGenerator(GeneratingOptions.builder().build());

        assertThat(generator.generate(List.of(column(), secondColumn)))
            .hasSize(2)
            .containsExactly(
                normalizeEndings("""
                    alter table if exists s1.t1
                        alter column col1 drop default;
                    drop sequence if exists s1.seq1;"""),
                normalizeEndings("""
                    alter table if exists s2.t2
                        alter column col2 drop default;
                    drop sequence if exists s2.seq2;""")
            );
    }

    @NonNull
    private ColumnWithSerialType column() {
        return ColumnWithSerialType.ofSerial(Column.ofNotNull("s1.t1", "col1"), "s1.seq1");
    }
}
