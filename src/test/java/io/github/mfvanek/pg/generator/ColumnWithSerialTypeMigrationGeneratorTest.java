/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.model.table.ColumnWithSerialType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("fast")
class ColumnWithSerialTypeMigrationGeneratorTest {

    @Test
    void shouldHandleInvalidArguments() {
        assertThatThrownBy(() -> new ColumnWithSerialTypeMigrationGenerator(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("options cannot be null");
    }

    @Test
    void forSingleColumn() {
        final ColumnWithSerialTypeMigrationGenerator generator = new ColumnWithSerialTypeMigrationGenerator(GeneratingOptions.builder().build());

        assertThat(generator.generate(Collections.singletonList(column())))
                .isEqualTo("alter table if exists s1.t1" + System.lineSeparator() +
                        "    alter column col1 drop default;" + System.lineSeparator() +
                        "drop sequence if exists s1.seq1;");
    }

    @Test
    void forSeveralColumns() {
        final ColumnWithSerialType secondColumn = ColumnWithSerialType.ofSerial(Column.ofNotNull("s2.t2", "col2"), "s2.seq2");
        final ColumnWithSerialTypeMigrationGenerator generator = new ColumnWithSerialTypeMigrationGenerator(GeneratingOptions.builder().build());

        assertThat(generator.generate(Arrays.asList(column(), secondColumn)))
                .isEqualTo("alter table if exists s1.t1" + System.lineSeparator() +
                        "    alter column col1 drop default;" + System.lineSeparator() +
                        "drop sequence if exists s1.seq1;" + System.lineSeparator() +
                        System.lineSeparator() +
                        "alter table if exists s2.t2" + System.lineSeparator() +
                        "    alter column col2 drop default;" + System.lineSeparator() +
                        "drop sequence if exists s2.seq2;");
    }

    @Nonnull
    private ColumnWithSerialType column() {
        return ColumnWithSerialType.ofSerial(Column.ofNotNull("s1.t1", "col1"), "s1.seq1");
    }
}
