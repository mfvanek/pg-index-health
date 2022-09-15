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

import io.github.mfvanek.pg.model.index.ForeignKey;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static io.github.mfvanek.pg.generator.PgIdentifierNameGeneratorTest.nullableColumnWithSchema;
import static io.github.mfvanek.pg.generator.PgIndexOnForeignKeyGeneratorTest.severalColumnsWithNulls;
import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class ForeignKeyMigrationGeneratorTest {

    @Test
    void generateForSingleForeignKey() {
        final DbMigrationGenerator<ForeignKey> generator = new ForeignKeyMigrationGenerator(GeneratingOptions.builder().build());
        final String result = generator.generate(Collections.singletonList(nullableColumnWithSchema()));
        assertThat(result)
                .isNotBlank()
                .isEqualTo("/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */" + System.lineSeparator() +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx" + System.lineSeparator() +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }

    @Test
    void generateForSeveralForeignKeys() {
        final DbMigrationGenerator<ForeignKey> generator = new ForeignKeyMigrationGenerator(GeneratingOptions.builder().build());
        final String result = generator.generate(
                Arrays.asList(severalColumnsWithNulls(), severalColumnsWithNulls(), nullableColumnWithSchema()));
        assertThat(result)
                .isNotBlank()
                .isEqualTo("create index concurrently if not exists table_column_1_column_2_without_nulls_idx" + System.lineSeparator() +
                        "    on table (column_1, column_2) where column_2 is not null;" + System.lineSeparator() +
                        System.lineSeparator() +
                        "create index concurrently if not exists table_column_1_column_2_without_nulls_idx" + System.lineSeparator() +
                        "    on table (column_1, column_2) where column_2 is not null;" + System.lineSeparator() +
                        System.lineSeparator() +
                        "/* table_with_very_very_very_long_name_column_with_very_very_very_long_name_without_nulls_idx */" + System.lineSeparator() +
                        "create index concurrently if not exists table_with_very_very_very_long_name_3202677_without_nulls_idx" + System.lineSeparator() +
                        "    on schema_name_that_should_be_omitted.table_with_very_very_very_long_name (column_with_very_very_very_long_name) " +
                        "where column_with_very_very_very_long_name is not null;");
    }
}
