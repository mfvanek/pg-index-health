/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DropSequenceGeneratorTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldHandleInvalidArguments() {
        assertThatThrownBy(() -> new DropSequenceGenerator(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("options cannot be null");

        final DropSequenceGenerator generator = new DropSequenceGenerator(GeneratingOptions.builder().build());
        assertThatThrownBy(() -> generator.generate(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
    }

    @Test
    void generateForColumnLowerCase() {
        final DropSequenceGenerator generator = new DropSequenceGenerator(GeneratingOptions.builder().build());
        assertThat(generator.generate(column()))
            .isEqualTo("drop sequence if exists s1.seq1;");
    }

    @Test
    void generateForColumnUpperCase() {
        final DropSequenceGenerator generator = new DropSequenceGenerator(
            GeneratingOptions.builder()
                .uppercaseForKeywords()
                .build());
        assertThat(generator.generate(column()))
            .isEqualTo("DROP SEQUENCE IF EXISTS s1.seq1;");
    }

    @Nonnull
    private ColumnWithSerialType column() {
        return ColumnWithSerialType.ofSerial(Column.ofNotNull("s1.t1", "col1"), "s1.seq1");
    }
}
